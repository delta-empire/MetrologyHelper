package ru.sergeipavlov.metrologyhelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Handles conversion between length units using BigDecimal for precision.
 */
public class LengthConverter {
    private static final int MAX_SIGNIFICANT_DIGITS = 34;
    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);

    /**
     * Convert value from one unit to another.
     *
     * @param value      the value to convert
     * @param fromUnit   source unit
     * @param toUnit     target unit
     * @return converted value
     */
    public static BigDecimal convert(BigDecimal value, LengthUnit fromUnit, LengthUnit toUnit) {
        if (value == null || fromUnit == null || toUnit == null) {
            return BigDecimal.ZERO;
        }

        // Convert to meters first
        BigDecimal meters = value.multiply(fromUnit.getToMetersCoefficient(), MC);

        // Then convert from meters to target unit
        BigDecimal result = meters.divide(toUnit.getToMetersCoefficient(), MC);

        return result;
    }

    /**
     * Format BigDecimal for display, removing trailing zeros.
     *
     * @param value value to format
     * @return formatted string
     */
    public static String formatForDisplay(BigDecimal value) {
        if (value == null) {
            return "0";
        }

        // Do not round all values to twelve decimal places: small dimensions are significant.
        BigDecimal rounded = value.round(new MathContext(MAX_SIGNIFICANT_DIGITS, RoundingMode.HALF_UP));
        return rounded.signum() == 0 ? "0" : rounded.stripTrailingZeros().toPlainString();
    }

    /**
     * Parse user input string to BigDecimal.
     * Handles both comma and dot as decimal separator.
     *
     * @param input user input string
     * @return parsed BigDecimal or null if invalid
     */
    public static BigDecimal parseInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String normalized = input.trim().replace(',', '.');

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Check if input string is valid (can be parsed to BigDecimal).
     *
     * @param input user input string
     * @return true if valid number format
     */
    public static boolean isValidInput(String input) {
        return parseInput(input) != null;
    }
}
