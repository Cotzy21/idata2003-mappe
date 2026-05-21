package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SaleCalculator Tests")
class SaleCalculatorTest {
    private SaleCalculator calculator;
    private Stock stock;
    private Share share;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("140.00"));
        calculator = new SaleCalculator(share);
    }

    @DisplayName("calculateGross: returns current price × quantity")
    @Test
    void testCalculateGross() {
        BigDecimal gross = calculator.calculateGross();
        // Stock sales price is 150.00, quantity is 10
        // 150.00 × 10 = 1500.00
        assertEquals(0, gross.compareTo(new BigDecimal("1500.00")));
    }

    @DisplayName("calculateCommission: returns 1% of gross")
    @Test
    void testCalculateCommission() {
        BigDecimal commission = calculator.calculateCommission();
        // 1500.00 × 0.01 = 15.00
        assertEquals(0, commission.compareTo(new BigDecimal("15.00")));
    }

    @DisplayName("calculateTax: returns 30% of profit")
    @Test
    void testCalculateTax_withProfit() {
        BigDecimal tax = calculator.calculateTax();
        // gross: 1500.00, commission: 15.00, cost: 140.00 × 10 = 1400.00
        // profit = 1500.00 - 15.00 - 1400.00 = 85.00
        // tax = 85.00 × 0.30 = 25.50
        assertEquals(0, tax.compareTo(new BigDecimal("25.50")));
    }

    @DisplayName("calculateTax: returns zero on loss")
    @Test
    void testCalculateTax_withLoss() {
        // Create share bought at higher price
        Stock stock2 = new Stock("TEST", "Test", new BigDecimal("100.00"));
        Share losShare = new Share(stock2, new BigDecimal("10"), new BigDecimal("120.00"));
        SaleCalculator saleCal = new SaleCalculator(losShare);

        BigDecimal tax = saleCal.calculateTax();
        // gross: 1000.00, commission: 10.00, cost: 120.00 × 10 = 1200.00
        // profit = 1000.00 - 10.00 - 1200.00 = -210.00 (loss)
        // tax = max(0, -210.00 × 0.30) = 0
        assertEquals(0, tax.compareTo(BigDecimal.ZERO));
    }

    @DisplayName("calculateTotal: returns gross - commission - tax")
    @Test
    void testCalculateTotal() {
        BigDecimal total = calculator.calculateTotal();
        // gross: 1500.00, commission: 15.00, tax: 25.50
        // total = 1500.00 - 15.00 - 25.50 = 1459.50
        assertEquals(0, total.compareTo(new BigDecimal("1459.50")));
    }
}

