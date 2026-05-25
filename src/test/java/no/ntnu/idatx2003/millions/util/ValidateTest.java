package no.ntnu.idatx2003.millions.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Validate utility")
class ValidateTest {

    @DisplayName("requireNonNull returns value when non-null")
    @Test
    void requireNonNull_whenValueIsNotNull_returnsValue() {
        Object input = new Object();
        assertSame(input, Validate.requireNonNull(input, "input"));
    }

    @DisplayName("requireNonNull throws NPE when null")
    @Test
    void requireNonNull_whenValueIsNull_throwsNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> Validate.requireNonNull(null, "input"));
        assertEquals("input must not be null", exception.getMessage());
    }

    @DisplayName("requireNotBlank returns value when text is present")
    @Test
    void requireNotBlank_whenTextIsPresent_returnsValue() {
        assertEquals("Alice", Validate.requireNotBlank("Alice", "name"));
    }

    @DisplayName("requireNotBlank throws when null")
    @Test
    void requireNotBlank_whenNull_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validate.requireNotBlank(null, "name"));
        assertEquals("name must not be null or blank", exception.getMessage());
    }

    @DisplayName("requireNotBlank throws when blank")
    @Test
    void requireNotBlank_whenBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requireNotBlank("   ", "name"));
    }

    @DisplayName("requirePositive returns value when positive")
    @Test
    void requirePositive_whenPositive_returnsValue() {
        BigDecimal value = new BigDecimal("0.01");
        assertSame(value, Validate.requirePositive(value, "amount"));
    }

    @DisplayName("requirePositive throws when zero")
    @Test
    void requirePositive_whenZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requirePositive(BigDecimal.ZERO, "amount"));
    }

    @DisplayName("requirePositive throws when negative")
    @Test
    void requirePositive_whenNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requirePositive(new BigDecimal("-1"), "amount"));
    }

    @DisplayName("requirePositive throws when null")
    @Test
    void requirePositive_whenNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requirePositive(null, "amount"));
    }

    @DisplayName("requireNonNegative accepts zero")
    @Test
    void requireNonNegative_whenZero_returnsValue() {
        assertSame(BigDecimal.ZERO, Validate.requireNonNegative(BigDecimal.ZERO, "amount"));
    }

    @DisplayName("requireNonNegative throws when negative")
    @Test
    void requireNonNegative_whenNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requireNonNegative(new BigDecimal("-0.01"), "amount"));
    }

    @DisplayName("requireInRange accepts boundary values")
    @Test
    void requireInRange_whenWithinRange_returnsValue() {
        assertEquals(0, Validate.requireInRange(0, 0, 10, "value"));
        assertEquals(10, Validate.requireInRange(10, 0, 10, "value"));
        assertEquals(5, Validate.requireInRange(5, 0, 10, "value"));
    }

    @DisplayName("requireInRange throws below minimum")
    @Test
    void requireInRange_whenBelowMin_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requireInRange(-1, 0, 10, "value"));
    }

    @DisplayName("requireInRange throws above maximum")
    @Test
    void requireInRange_whenAboveMax_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Validate.requireInRange(11, 0, 10, "value"));
    }
}
