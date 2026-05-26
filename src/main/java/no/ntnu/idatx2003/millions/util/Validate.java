package no.ntnu.idatx2003.millions.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Centralised validation utilities for public parameters.
 *
 * <p>All methods either return the validated value on success or raise an
 * {@link IllegalArgumentException} / {@link NullPointerException} with a
 * descriptive message that identifies the offending parameter by name. The
 * intent is to remove repetitive inline validation throughout the domain
 * layer while keeping error messages consistent.</p>
 */
public final class Validate {

    private Validate() {
        // Utility class.
    }

    /**
     * Ensures {@code value} is not {@code null}.
     *
     * @param value the value to inspect
     * @param name the name of the validated parameter
     * @param <T> the value type
     * @return the validated value
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public static <T> T requireNonNull(T value, String name) {
        return Objects.requireNonNull(value, () -> name + " must not be null");
    }

    /**
     * Ensures {@code value} is non-{@code null} and not blank.
     *
     * @param value the value to inspect
     * @param name the name of the validated parameter
     * @return the validated value
     * @throws IllegalArgumentException if {@code value} is {@code null} or blank
     */
    public static String requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
        return value;
    }

    /**
     * Ensures {@code value} is non-{@code null} and strictly greater than zero.
     *
     * @param value the value to inspect
     * @param name the name of the validated parameter
     * @return the validated value
     * @throws IllegalArgumentException if {@code value} is {@code null} or non-positive
     */
    public static BigDecimal requirePositive(BigDecimal value, String name) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }

    /**
     * Ensures {@code value} is non-{@code null} and zero or greater.
     *
     * @param value the value to inspect
     * @param name the name of the validated parameter
     * @return the validated value
     * @throws IllegalArgumentException if {@code value} is {@code null} or negative
     */
    public static BigDecimal requireNonNegative(BigDecimal value, String name) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(name + " must be zero or greater");
        }
        return value;
    }

    /**
     * Ensures {@code value} lies within the inclusive {@code [min, max]} range.
     *
     * @param value the value to inspect
     * @param min the inclusive lower bound
     * @param max the inclusive upper bound
     * @param name the name of the validated parameter
     * @return the validated value
     * @throws IllegalArgumentException if {@code value} is outside the range
     */
    public static int requireInRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    name + " must be in [" + min + ", " + max + "] but was " + value);
        }
        return value;
    }
}
