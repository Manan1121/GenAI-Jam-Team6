"""Unit tests for the Python port of the legacy Perl ETL script."""

from __future__ import annotations

import contextlib
import io
import tempfile
import unittest
from pathlib import Path

import etl


class EtlTests(unittest.TestCase):
    def setUp(self) -> None:
        """Point integration tests at the real challenge fixture files."""
        self.challenge_dir = Path(__file__).resolve().parent

    def test_normalize_headers_matches_perl_cleanup_intent(self) -> None:
        """Cover the header sanitising block from the old Perl script."""
        headers = ['Ticker Symbol', 'Close Price', 'Issuer\'s Note', '"Quoted"']
        self.assertEqual(
            etl.normalize_headers(headers),
            ['Ticker_Symbol', 'Close_Price', 'Issuer\\\'s_Note', 'Quoted'],
        )

    def test_infer_profiles_preserves_empty_value_and_type_rules(self) -> None:
        """Cover the Perl field inference block, including empty-value quirks."""
        headers = ["symbol", "trade_price", "notes", "strange"]
        rows = [
            ["ABC", "12.34", "", "1.2.3"],
            ["XYZ", "", "alpha", "5"],
        ]

        profiles = etl.infer_profiles(headers, rows)

        self.assertEqual(profiles[0].schema_fragment(), "`symbol` varchar(3)")
        self.assertEqual(profiles[1].schema_fragment(), "`trade_price` decimal(4,2)")
        self.assertEqual(profiles[2].schema_fragment(), "`notes` varchar(5)")
        self.assertEqual(profiles[3].schema_fragment(), "`strange` varchar(5)")

    def test_build_insert_sql_quotes_and_escapes_values(self) -> None:
        """Cover the Perl INSERT assembly block, including apostrophe escaping."""
        sql = etl.build_insert_sql(
            table_name="prices",
            headers=["name", "value"],
            rows=[["O'Brien", ""], ["ACME", "42"]],
        )
        self.assertEqual(
            sql,
            "INSERT INTO prices (name,value) VALUES ('O\\'Brien', '');\n"
            "INSERT INTO prices (name,value) VALUES ('ACME', '42');",
        )

    def test_generate_sql_files_recreates_expected_artifacts_for_fixture(self) -> None:
        """Cover the full Perl workflow against the real challenge CSV fixture."""
        csv_path = self.challenge_dir / "prices.csv"
        expected_insert_sql = (self.challenge_dir / "mysqlInsertValues.sql").read_text(encoding="utf-8")

        with tempfile.TemporaryDirectory() as temp_dir:
            temp_path = Path(temp_dir)
            schema_path = temp_path / "schema.sql"
            values_path = temp_path / "values.sql"

            schema_sql, insert_sql, column_count, row_count = etl.generate_sql_files(
                csv_path=csv_path,
                schema_path=schema_path,
                values_path=values_path,
            )

            self.assertEqual(column_count, 7)
            self.assertGreater(row_count, 1000)
            self.assertEqual(insert_sql, expected_insert_sql)
            self.assertEqual(schema_path.read_text(encoding="utf-8"), schema_sql)
            self.assertEqual(values_path.read_text(encoding="utf-8"), insert_sql)

        self.assertEqual(
            schema_sql,
            "CREATE TABLE `nasdaq_prices` (\n"
            "`ticker` varchar(5),\n"
            "`date` int(8),\n"
            "`open` decimal(8,4),\n"
            "`high` decimal(7,4),\n"
            "`low` decimal(8,4),\n"
            "`close` decimal(7,4),\n"
            "`vol` int(8)\n"
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",
        )

    def test_main_prints_the_same_status_summary_as_the_perl_script(self) -> None:
        """Cover the Perl-style completion message at the script entry point."""
        csv_path = self.challenge_dir / "prices.csv"

        with tempfile.TemporaryDirectory() as temp_dir:
            temp_path = Path(temp_dir)
            stdout = io.StringIO()
            with contextlib.redirect_stdout(stdout):
                exit_code = etl.main(
                    [
                        str(csv_path),
                        "--schema-output",
                        str(temp_path / "schema.sql"),
                        "--values-output",
                        str(temp_path / "values.sql"),
                    ]
                )

        self.assertEqual(exit_code, 0)
        self.assertIn("Processed 7 columns", stdout.getvalue())


if __name__ == "__main__":
    unittest.main()