package no.ntnu.idatx2003.millions.model;

import java.math.BigDecimal;

/**
 * Represents a share (ownership) of a stock.
 * Stores the stock, quantity held, and purchase price.
 */
public class Share {
    private final Stock stock;
    private final BigDecimal quantity;
    private final BigDecimal purchasePrice;

    /**
     * Constructs a Share.
     *
     * @param stock the Stock object
     * @param quantity the number of shares held
     * @param purchasePrice the price at which the shares were purchased
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Purchase price must be non-negative");
        }

        this.stock = stock;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    /**
     * Gets the stock.
     *
     * @return the Stock object
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Gets the quantity of shares.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Gets the purchase price per share.
     *
     * @return the purchase price
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    @Override
    public String toString() {
        return String.format("%s x %s @ %s", quantity, stock.getSymbol(), purchasePrice);
    }
}


