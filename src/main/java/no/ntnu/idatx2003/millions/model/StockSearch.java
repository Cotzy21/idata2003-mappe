package no.ntnu.idatx2003.millions.model;

import no.ntnu.idatx2003.millions.util.Validate;

/**
 * Case-insensitive matching of {@link Stock} instances against free-text search terms.
 *
 * <p>Centralises the search predicate so that both
 * {@link Exchange#findStocks(String)} and any GUI filter (such as the market
 * table search field) share a single implementation.</p>
 */
public final class StockSearch {

    private StockSearch() {
        // Utility class.
    }

    /**
     * Returns whether a stock matches the given search term.
     *
     * <p>The match is case-insensitive and tests both the symbol and the
     * company name as substrings. A {@code null} or blank search term matches
     * every stock.</p>
     *
     * @param stock the stock to inspect; must not be {@code null}
     * @param searchTerm the search term, may be {@code null} or blank
     * @return {@code true} if the stock matches
     */
    public static boolean matches(Stock stock, String searchTerm) {
        Validate.requireNonNull(stock, "stock");
        if (searchTerm == null || searchTerm.isBlank()) {
            return true;
        }
        String lower = searchTerm.toLowerCase();
        return stock.getSymbol().toLowerCase().contains(lower)
                || stock.getCompany().toLowerCase().contains(lower);
    }
}
