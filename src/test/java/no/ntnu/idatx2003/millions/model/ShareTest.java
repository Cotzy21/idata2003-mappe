package no.ntnu.idatx2003.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Share Tests")
class ShareTest {
    private Stock stock;
    private Share share;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
    }

    @DisplayName("Constructor: creates share with valid inputs")
    @Test
    void testConstructor_validInputs() {
        assertEquals(stock, share.getStock());
        assertEquals(0, share.getQuantity().compareTo(new BigDecimal("10")));
        assertEquals(0, share.getPurchasePrice().compareTo(new BigDecimal("148.00")));
    }

    @DisplayName("Constructor: throws exception for null stock")
    @Test
    void testConstructor_nullStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new Share(null, new BigDecimal("10"), new BigDecimal("148")));
    }

    @DisplayName("Constructor: throws exception for zero quantity")
    @Test
    void testConstructor_zeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Share(stock, BigDecimal.ZERO, new BigDecimal("148")));
    }

    @DisplayName("Constructor: throws exception for negative quantity")
    @Test
    void testConstructor_negativeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Share(stock, new BigDecimal("-5"), new BigDecimal("148")));
    }

    @DisplayName("Constructor: throws exception for negative price")
    @Test
    void testConstructor_negativePurchasePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Share(stock, new BigDecimal("10"), new BigDecimal("-148")));
    }

    @DisplayName("Getters: returns correct values")
    @Test
    void testGetters() {
        assertEquals(stock, share.getStock());
        assertEquals(0, share.getQuantity().compareTo(new BigDecimal("10")));
        assertEquals(0, share.getPurchasePrice().compareTo(new BigDecimal("148.00")));
    }
}

