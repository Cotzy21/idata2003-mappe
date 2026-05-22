package no.ntnu.idatx2003.millions.model.transaction;

import java.math.BigDecimal;

/**
 * Interface for calculating transaction costs and totals.
 * Implementing classes provide different calculation strategies for purchases and sales.
 */
public interface TransactionCalculator {
    /**
     * Calculates the gross amount (before commissions and taxes).
     *
     * @return the gross amount
     */
    BigDecimal calculateGross();

    /**
     * Calculates the commission fee.
     *
     * @return the commission amount
     */
    BigDecimal calculateCommission();

    /**
     * Calculates the tax amount.
     *
     * @return the tax amount
     */
    BigDecimal calculateTax();

    /**
     * Calculates the total amount (after all deductions).
     *
     * @return the total cost or proceeds
     */
    BigDecimal calculateTotal();
}

