package ru.sergeipavlov.metrologyhelper;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LengthConverterTest {

    private static final double DELTA = 0.0000001;

    @Test
    public void test_1000mm_equals_1m() {
        LengthUnit millimeter = LengthUnits.getUnitById("millimeter");
        LengthUnit meter = LengthUnits.getUnitById("meter");

        BigDecimal result = LengthConverter.convert(
                new BigDecimal("1000"), millimeter, meter);

        assertEquals(1.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_254cm_equals_1inch() {
        LengthUnit centimeter = LengthUnits.getUnitById("centimeter");
        LengthUnit inch = LengthUnits.getUnitById("inch");

        BigDecimal result = LengthConverter.convert(
                new BigDecimal("2.54"), centimeter, inch);

        assertEquals(1.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_1km_equals_1000m() {
        LengthUnit kilometer = LengthUnits.getUnitById("kilometer");
        LengthUnit meter = LengthUnits.getUnitById("meter");

        BigDecimal result = LengthConverter.convert(
                new BigDecimal("1"), kilometer, meter);

        assertEquals(1000.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_zero_values() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        LengthUnit foot = LengthUnits.getUnitById("foot");

        BigDecimal result = LengthConverter.convert(
                BigDecimal.ZERO, meter, foot);

        assertEquals(0.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_negative_values() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        LengthUnit centimeter = LengthUnits.getUnitById("centimeter");

        BigDecimal result = LengthConverter.convert(
                new BigDecimal("-5"), meter, centimeter);

        assertEquals(-500.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_decimal_values() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        LengthUnit millimeter = LengthUnits.getUnitById("millimeter");

        BigDecimal result = LengthConverter.convert(
                new BigDecimal("0.5"), meter, millimeter);

        assertEquals(500.0, result.doubleValue(), DELTA);
    }

    @Test
    public void test_reverse_conversion() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        LengthUnit inch = LengthUnits.getUnitById("inch");

        BigDecimal original = new BigDecimal("10");

        BigDecimal inchValue = LengthConverter.convert(original, meter, inch);
        BigDecimal backToMeters = LengthConverter.convert(inchValue, inch, meter);

        assertEquals(original.doubleValue(), backToMeters.doubleValue(), DELTA);
    }

    @Test
    public void test_parseInput_with_comma() {
        BigDecimal result = LengthConverter.parseInput("3,14");
        assertNotNull(result);
        assertEquals(3.14, result.doubleValue(), DELTA);
    }

    @Test
    public void test_parseInput_with_dot() {
        BigDecimal result = LengthConverter.parseInput("3.14");
        assertNotNull(result);
        assertEquals(3.14, result.doubleValue(), DELTA);
    }

    @Test
    public void test_parseInput_empty_string() {
        BigDecimal result = LengthConverter.parseInput("");
        assertNull(result);
    }

    @Test
    public void test_parseInput_invalid_string() {
        BigDecimal result = LengthConverter.parseInput("abc");
        assertNull(result);
    }

    @Test
    public void test_formatForDisplay_removes_trailing_zeros() {
        BigDecimal value = new BigDecimal("10.00000");
        String result = LengthConverter.formatForDisplay(value);
        assertEquals("10", result);
    }

    @Test
    public void test_formatForDisplay_preserves_decimals() {
        BigDecimal value = new BigDecimal("10.123");
        String result = LengthConverter.formatForDisplay(value);
        assertEquals("10.123", result);
    }

    @Test
    public void test_null_values_return_zero() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        BigDecimal result = LengthConverter.convert(null, meter, meter);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void test_same_unit_conversion() {
        LengthUnit meter = LengthUnits.getUnitById("meter");
        BigDecimal original = new BigDecimal("42");

        BigDecimal result = LengthConverter.convert(original, meter, meter);

        assertEquals(original.doubleValue(), result.doubleValue(), DELTA);
    }

    @Test
    public void test_siBaseUnit_is_meter() {
        LengthUnit siUnit = LengthUnits.getSIBaseUnit();
        assertEquals("meter", siUnit.getId());
        assertTrue(siUnit.isSI());
    }

    @Test
    public void test_nautical_mile_conversion() {
        LengthUnit nauticalMile = LengthUnits.getUnitById("nautical_mile");
        LengthUnit meter = LengthUnits.getUnitById("meter");

        BigDecimal result = LengthConverter.convert(
                BigDecimal.ONE, nauticalMile, meter);

        assertEquals(1852.0, result.doubleValue(), DELTA);
    }
}
