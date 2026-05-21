package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Share;

/**
 * Factory for creating transaction objects.
 * Implements the Factory design pattern.
 */
public class TransactionFactory {
    /**
     * Private constructor to prevent instantiation.
     */
    private TransactionFactory() {
        // Factory class should not be instantiated
    }

    /**
     * Creates a Purchase transaction.
     *
     * @param share the Share being purchased
     * @param week the week in which the purchase occurs
     * @return a new Purchase transaction
     */
    public static Purchase createPurchase(Share share, int week) {
        PurchaseCalculator calculator = new PurchaseCalculator(share);
        return new Purchase(share, week, calculator);
    }

    /**
     * Creates a Sale transaction.
     *
     * @param share the Share being sold
     * @param week the week in which the sale occurs
     * @return a new Sale transaction
     */
    public static Sale createSale(Share share, int week) {
        SaleCalculator calculator = new SaleCalculator(share);
        return new Sale(share, week, calculator);
    }
}

