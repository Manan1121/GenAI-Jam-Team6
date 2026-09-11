public class ValidateISBN {

	private static final int LONG_ISBN_MULTIPLIER = 10;
	private static final int SHORT_ISBN_MULTIPLIER = 11;
	private static final int SHORT_ISBN_LENGTH = 10;
	private static final int LONG_ISBN_LENGTH = 13;
	private static final int CHECK_DIGIT_POSITION_SHORT = 9;
	private static final char CHECK_DIGIT_X = 'X';
	private static final int CHECK_DIGIT_X_VALUE = 10;

	/**
	 * Validates an ISBN number (10 or 13 digits).
	 * ISBN-10: 10 digits where the last digit can be 0-9 or 'X' (representing 10)
	 * ISBN-13: 13 digits, all numeric
	 * 
	 * @param isbn the ISBN string to validate
	 * @return true if the ISBN is valid, false otherwise
	 * @throws NumberFormatException if ISBN is null, not 10 or 13 digits, or contains invalid characters
	 */
	public boolean checkISBN(String isbn) {
		if (isbn == null || isbn.isEmpty()) {
			throw new NumberFormatException("ISBN cannot be null or empty");
		}

		if (isbn.length() == LONG_ISBN_LENGTH) {
			return isThisAValidLongISBN(isbn);
		}
		else if (isbn.length() == SHORT_ISBN_LENGTH) {
			return isThisAValidShortISBN(isbn);			
		}
		throw new NumberFormatException("ISBN must be exactly 10 or 13 characters long (provided: " + isbn.length() + ")");
	}

	/**
	 * Validates a 10-digit ISBN.
	 * Calculation: sum of (digit * position) where position is 10, 9, 8, ... 1
	 * The check digit (last position) can be 0-9 or 'X' (representing 10)
	 * Valid ISBN-10 has a checksum divisible by 11
	 */
	private boolean isThisAValidShortISBN(String isbn) {
		int total = 0;

		for (int i = 0; i < SHORT_ISBN_LENGTH; i++)
		{
			if (!Character.isDigit(isbn.charAt(i))) {
				if (i == CHECK_DIGIT_POSITION_SHORT && isbn.charAt(i) == CHECK_DIGIT_X) {
					total += CHECK_DIGIT_X_VALUE;
				}
				else {
					throw new NumberFormatException("ISBN-10 must contain only digits, except position 10 which may be 'X' (found '" + isbn.charAt(i) + "' at position " + (i + 1) + ")");
				}
			}
			else {
				// BUG FIX: Use Character.getNumericValue() instead of ASCII value
				// OLD (BUGGY): total += isbn.charAt(i) * (SHORT_ISBN_LENGTH - i);
				// This was using ASCII values (e.g., '0' = 48, '1' = 49) instead of numeric values (0, 1, etc.)
				total += Character.getNumericValue(isbn.charAt(i)) * (SHORT_ISBN_LENGTH - i);
			}
		}

		return (total % SHORT_ISBN_MULTIPLIER == 0);
	}

	/**
	 * Validates a 13-digit ISBN.
	 * Calculation: sum of digits at alternating positions multiplied by 1 and 3
	 * Positions: 1st digit * 1, 2nd digit * 3, 3rd digit * 1, 4th digit * 3, etc.
	 * Valid ISBN-13 has a checksum divisible by 10
	 */
	private boolean isThisAValidLongISBN(String isbn) {
		int total = 0;
		
		for (int i = 0; i < LONG_ISBN_LENGTH; i++) {
			// Validate that all characters are digits
			if (!Character.isDigit(isbn.charAt(i))) {
				throw new NumberFormatException("ISBN-13 must contain only numeric digits (found '" + isbn.charAt(i) + "' at position " + (i + 1) + ")");
			}
			
			if (i % 2 == 0) {
				// BUG FIX: Use Character.getNumericValue() instead of ASCII value
				// OLD (BUGGY): total += isbn.charAt(i);
				// This was using ASCII values (e.g., '0' = 48, '1' = 49) instead of numeric values (0, 1, etc.)
				total += Character.getNumericValue(isbn.charAt(i));
			}
			else {
				// BUG FIX: Use Character.getNumericValue() instead of ASCII value
				// OLD (BUGGY): total += isbn.charAt(i) * 3;
				// This was using ASCII values (e.g., '0' = 48, '1' = 49) instead of numeric values (0, 1, etc.)
				total += Character.getNumericValue(isbn.charAt(i)) * 3;
			}
		}
		return (total % LONG_ISBN_MULTIPLIER == 0);
	}
}
