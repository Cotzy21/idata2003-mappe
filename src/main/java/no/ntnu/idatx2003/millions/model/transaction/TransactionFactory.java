package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Share;

import java.math.BigDecimal;

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

    /**
     * Creates a Sale transaction for a specified quantity of a portfolio share.
     *
     * @param portfolioShare the Share currently held in the portfolio
     * @param quantity the quantity to sell
     * @param week the week in which the sale occurs
     * @return a new Sale transaction
     */
    public static Sale createSale(Share portfolioShare, BigDecimal quantity, int week) {
        if (portfolioShare == null) {
            throw new IllegalArgumentException("Portfolio share cannot be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sale quantity must be positive");
        }
        if (quantity.compareTo(portfolioShare.getQuantity()) > 0) {
            throw new IllegalArgumentException("Cannot sell more shares than the portfolio contains");
        }

        Share saleShare = new Share(
                portfolioShare.getStock(),
                quantity,
                portfolioShare.getPurchasePrice());
        SaleCalculator calculator = new SaleCalculator(saleShare);
        return new Sale(saleShare, portfolioShare, week, calculator);
    }
}
