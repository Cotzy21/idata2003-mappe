package no.ntnu.idatx2003.millions.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a player's portfolio of shares.
 * Manages adding, removing, and querying shares.
 */
public class Portfolio {
    private final List<Share> shares;

    /**
     * Constructs an empty Portfolio.
     */
    public Portfolio() {
        this.shares = new ArrayList<>();
    }

    /**
     * Adds a share to the portfolio.
     *
     * @param share the Share to add
     * @return true if successfully added, false if null
     */
    public boolean addShare(Share share) {
        if (share == null) {
            return false;
        }
        return shares.add(share);
    }

    /**
     * Removes a share from the portfolio.
     *
     * @param share the Share to remove
     * @return true if successfully removed, false if not found or null
     */
    public boolean removeShare(Share share) {
        if (share == null) {
            return false;
        }
        return shares.remove(share);
    }

    /**
     * Gets all shares in the portfolio.
     *
     * @return an unmodifiable copy of the share list
     */
    public List<Share> getShares() {
        return List.copyOf(shares);
    }

    /**
     * Gets all shares of a specific stock symbol.
     *
     * @param symbol the stock symbol
     * @return an unmodifiable list of matching shares
     */
    public List<Share> getShares(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            return List.of();
        }
        return shares.stream()
                .filter(share -> share.getStock().getSymbol().equals(symbol))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Checks if the portfolio contains a specific share.
     *
     * @param share the Share to check
     * @return true if contained, false otherwise
     */
    public boolean contains(Share share) {
        return shares.contains(share);
    }

    /**
     * Calculates the net worth of the portfolio based on current sales prices.
     *
     * @return the total current value of all shares
     */
    public BigDecimal getNetWorth() {
        return shares.stream()
                .map(share -> share.getStock().getSalesPrice().multiply(share.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return String.format("Portfolio with %d shares, worth %s",
                shares.size(), getNetWorth());
    }
}

