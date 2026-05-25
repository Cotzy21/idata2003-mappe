package no.ntnu.idatx2003.millions.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock on the exchange.
 * Contains symbol, company name, and a list of historical prices.
 */
public class Stock {
    private final String symbol;
    private final String company;
    private final List<BigDecimal> prices;

    /**
     * Constructs a Stock with initial sales price.
     *
     * @param symbol the stock symbol (e.g., "AAPL")
     * @param company the company name (e.g., "Apple Inc.")
     * @param initialPrice the initial sales price
     * @throws IllegalArgumentException if symbol or company is empty, or price is not positive
     */
    public Stock(String symbol, String company, BigDecimal initialPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (company == null || company.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null or empty");
        }
        if (initialPrice == null || initialPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Initial price must be positive");
        }

        this.symbol = symbol;
        this.company = company;
        this.prices = new ArrayList<>();
        this.prices.add(initialPrice);
    }

    /**
     * Gets the stock symbol.
     *
     * @return the symbol (e.g., "AAPL")
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the company name.
     *
     * @return the company name
     */
    public String getCompany() {
        return company;
    }

    /**
     * Gets the current (latest) sales price.
     *
     * @return the most recent price in the list
     */
    public BigDecimal getSalesPrice() {
        return prices.get(prices.size() - 1);
    }

    /**
     * Adds a new sales price to the history.
     *
     * @param price the new price
     * @throws IllegalArgumentException if price is null or negative
     */
    public void addNewSalesPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        prices.add(price);
    }

    /**
     * Gets the complete list of historical prices.
     *
     * @return an unmodifiable copy of the price list
     */
    public List<BigDecimal> getHistoricalPrices() {
        return List.copyOf(prices);
    }

    /**
     * Gets the highest price in history.
     *
     * @return the maximum price
     */
    public BigDecimal getHighestPrice() {
        return prices.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Gets the lowest price in history.
     *
     * @return the minimum price
     */
    public BigDecimal getLowestPrice() {
        return prices.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Gets the latest price change (difference between last and second-last price).
     *
     * @return the price difference, or ZERO if only one price exists
     */
    public BigDecimal getLatestPriceChange() {
        if (prices.size() < 2) {
            return BigDecimal.ZERO;
        }
        BigDecimal latest = prices.get(prices.size() - 1);
        BigDecimal previous = prices.get(prices.size() - 2);
        return latest.subtract(previous);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", symbol, company, getSalesPrice());
    }
}
