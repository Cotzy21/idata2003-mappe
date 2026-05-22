package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PurchaseCalculator Tests")
class PurchaseCalculatorTest {
    private PurchaseCalculator calculator;
    private Stock stock;
    private Share share;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        calculator = new PurchaseCalculator(share);
    }

    @DisplayName("calculateGross: returns price × quantity")
    @Test
    void testCalculateGross() {
        BigDecimal gross = calculator.calculateGross();
        // 148.00 × 10 = 1480.00
        assertEquals(0, gross.compareTo(new BigDecimal("1480.00")));
    }

    @DisplayName("calculateCommission: returns 0.5% of gross")
    @Test
    void testCalculateCommission() {
        BigDecimal commission = calculator.calculateCommission();
        // 1480.00 × 0.005 = 7.40
        assertEquals(0, commission.compareTo(new BigDecimal("7.40")));
    }

    @DisplayName("calculateTax: returns zero")
    @Test
    void testCalculateTax() {
        BigDecimal tax = calculator.calculateTax();
        assertEquals(0, tax.compareTo(BigDecimal.ZERO));
    }

    @DisplayName("calculateTotal: returns gross + commission")
    @Test
    void testCalculateTotal() {
        BigDecimal total = calculator.calculateTotal();
        // 1480.00 + 7.40 = 1487.40
        assertEquals(0, total.compareTo(new BigDecimal("1487.40")));
    }
}

