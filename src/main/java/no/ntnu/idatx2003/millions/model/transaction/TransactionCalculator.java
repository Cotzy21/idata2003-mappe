package no.ntnu.idatx2003.millions.model.transaction;

import java.math.BigDecimal;

/**
 * Strategy interface for computing the financial breakdown of a transaction.
 *
 * <p>This is an explicit application of the <em>Strategy</em> pattern. Each
 * transaction type has different rules for gross amounts, commissions and
 * taxes; the strategy is selected by {@link TransactionFactory} when a
 * transaction is built, and the {@link Transaction} simply delegates to its
 * calculator at execute time. Adding a new transaction type means adding a
 * new {@code TransactionCalculator} implementation; no existing code has to
 * change.</p>
 *
 * <p>Known implementations:</p>
 * <ul>
 *     <li>{@link PurchaseCalculator} – buying shares.</li>
 *     <li>{@link SaleCalculator} – selling shares.</li>
 * </ul>
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
