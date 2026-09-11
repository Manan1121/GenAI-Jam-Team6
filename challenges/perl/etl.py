"""Modern Python port of the legacy Perl ETL script.

The original Perl program read ``prices.csv``, normalised the header names,
guessed MySQL column types by scanning every row, then wrote two SQL files:
``mysqlCreateSchema.sql`` with a ``CREATE TABLE`` statement and
``mysqlInsertValues.sql`` with one ``INSERT`` statement per CSV record.

This Python version mirrors the same overall workflow while making the logic
testable and maintainable. It intentionally preserves two notable Perl-era
behaviours because they affect the generated SQL: empty values are treated as
``0`` while inferring column types, and every emitted ``INSERT`` value is still
single-quoted regardless of the inferred SQL type.
"""

from __future__ import annotations

import argparse
import csv
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, Sequence


# This dataclass corresponds to the Perl arrays that accumulated type metadata.
@dataclass(slots=True)
class ColumnProfile:
    name: str
    sql_type: str = "int"
    length: int = 0
    integer_digits: int = 0
    fractional_digits: int = 0

    def apply_value(self, value: str) -> None:
        """Mirror the Perl per-field type inference in one focused method."""
        escaped_value = escape_sql_value(value)
        value_for_inference = escaped_value or "0"
        value_length = len(value_for_inference)

        if any(character.isalpha() for character in value_for_inference):
            self.sql_type = "varchar"
            self.length = max(self.length, value_length)
            return

        if self.sql_type == "varchar":
            self.length = max(self.length, value_length)
            return

        if any(not character.isalpha() for character in value_for_inference):
            if self.sql_type != "decimal":
                self.sql_type = "int"
                self.length = max(self.length, value_length)

        if any(character.isdigit() or character == "." for character in value_for_inference):
            period_count = value_for_inference.count(".")
            if period_count > 1:
                self.sql_type = "varchar"
                self.length = max(self.length, value_length)
                self.integer_digits = 0
                self.fractional_digits = 0
                return

            if period_count == 1:
                integer_part, fractional_part = value_for_inference.split(".", maxsplit=1)
                self.sql_type = "decimal"
                self.integer_digits = max(self.integer_digits, len(integer_part))
                self.fractional_digits = max(self.fractional_digits, len(fractional_part))
                return

        if any(not (character.isdigit() or character == ".") for character in value_for_inference):
            self.sql_type = "varchar"
            self.length = max(self.length, value_length)

    def schema_fragment(self) -> str:
        """Emit the SQL type fragment that the Perl script printed at the end."""
        if self.sql_type == "decimal":
            total_digits = self.integer_digits + self.fractional_digits
            return f"`{self.name}` decimal({total_digits},{self.fractional_digits})"
        return f"`{self.name}` {self.sql_type}({self.length})"


# This helper corresponds to the Perl header cleanup before splitting columns.
def normalize_headers(headers: Sequence[str]) -> list[str]:
    return [header.replace("'", "\\'").replace('"', "").strip().replace(" ", "_") for header in headers]


# This helper corresponds to the Perl substitution that escaped apostrophes.
def escape_sql_value(value: str) -> str:
    return value.replace("'", "\\'")


# This loader corresponds to the Perl file-open and row-splitting setup.
def read_csv_rows(csv_path: Path) -> tuple[list[str], list[list[str]]]:
    with csv_path.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.reader(handle)
        raw_headers = next(reader)
        headers = normalize_headers(raw_headers)
        rows = [list(row) for row in reader]
    return headers, rows


# This builder corresponds to the Perl loop that accumulated column metadata.
def infer_profiles(headers: Sequence[str], rows: Iterable[Sequence[str]]) -> list[ColumnProfile]:
    profiles = [ColumnProfile(name=header) for header in headers]
    for row in rows:
        for profile, value in zip(profiles, row, strict=True):
            profile.apply_value(value)
    return profiles


# This formatter corresponds to the Perl string assembly for insert statements.
def build_insert_sql(table_name: str, headers: Sequence[str], rows: Iterable[Sequence[str]]) -> str:
    column_list = ",".join(headers)
    statements: list[str] = []
    for row in rows:
        escaped_values = [escape_sql_value(value) for value in row]
        quoted_values = ", ".join(f"'{value}'" for value in escaped_values)
        statements.append(f"INSERT INTO {table_name} ({column_list}) VALUES ({quoted_values});")
    return "\n".join(statements)


# This formatter corresponds to the Perl CREATE TABLE output block.
def build_schema_sql(table_name: str, profiles: Sequence[ColumnProfile], engine: str, charset: str) -> str:
    column_definitions = ",\n".join(profile.schema_fragment() for profile in profiles)
    return (
        f"CREATE TABLE `{table_name}` (\n"
        f"{column_definitions}\n"
        f") ENGINE={engine} DEFAULT CHARSET={charset};\n"
    )


# This orchestrator corresponds to the Perl main body that read, inferred, and wrote files.
def generate_sql_files(
    csv_path: Path,
    schema_path: Path,
    values_path: Path,
    table_name: str = "nasdaq_prices",
    engine: str = "InnoDB",
    charset: str = "latin1",
) -> tuple[str, str, int, int]:
    headers, rows = read_csv_rows(csv_path)
    profiles = infer_profiles(headers, rows)
    schema_sql = build_schema_sql(table_name, profiles, engine, charset)
    insert_sql = build_insert_sql(table_name, headers, rows)
    schema_path.write_text(schema_sql, encoding="utf-8")
    values_path.write_text(insert_sql, encoding="utf-8")
    return schema_sql, insert_sql, len(headers), len(rows)


# This parser corresponds to the hard-coded Perl configuration, but makes it configurable.
def build_argument_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Convert a CSV price file into MySQL schema and insert scripts.")
    parser.add_argument("csv_path", nargs="?", default="prices.csv", help="Input CSV file to transform.")
    parser.add_argument("--schema-output", default="mysqlCreateSchema.sql", help="Path for the CREATE TABLE script.")
    parser.add_argument("--values-output", default="mysqlInsertValues.sql", help="Path for the INSERT statements script.")
    parser.add_argument("--table-name", default="nasdaq_prices", help="Target SQL table name.")
    parser.add_argument("--engine", default="InnoDB", help="MySQL storage engine to emit.")
    parser.add_argument("--charset", default="latin1", help="MySQL character set to emit.")
    return parser


# This entry point corresponds to the Perl script execution path and status print.
def main(argv: Sequence[str] | None = None) -> int:
    parser = build_argument_parser()
    args = parser.parse_args(argv)

    csv_path = Path(args.csv_path)
    schema_path = Path(args.schema_output)
    values_path = Path(args.values_output)

    _, _, column_count, row_count = generate_sql_files(
        csv_path=csv_path,
        schema_path=schema_path,
        values_path=values_path,
        table_name=args.table_name,
        engine=args.engine,
        charset=args.charset,
    )

    print(f"Processed {column_count} columns and {row_count} lines.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())