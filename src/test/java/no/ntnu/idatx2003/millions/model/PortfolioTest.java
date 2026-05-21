package no.ntnu.idatx2003.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Portfolio Tests")
class PortfolioTest {
    private Portfolio portfolio;
    private Stock stock;
    private Share share;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
    }

    @DisplayName("Constructor: creates empty portfolio")
    @Test
    void testConstructor() {
        assertTrue(portfolio.getShares().isEmpty());
    }

    @DisplayName("addShare: adds share successfully")
    @Test
    void testAddShare_success() {
        assertTrue(portfolio.addShare(share));
        assertEquals(1, portfolio.getShares().size());
    }

    @DisplayName("addShare: returns false for null share")
    @Test
    void testAddShare_null() {
        assertFalse(portfolio.addShare(null));
    }

    @DisplayName("removeShare: removes share successfully")
    @Test
    void testRemoveShare_success() {
        portfolio.addShare(share);
        assertTrue(portfolio.removeShare(share));
        assertTrue(portfolio.getShares().isEmpty());
    }

    @DisplayName("removeShare: returns false when share not present")
    @Test
    void testRemoveShare_notPresent() {
        assertFalse(portfolio.removeShare(share));
    }

    @DisplayName("removeShare: returns false for null")
    @Test
    void testRemoveShare_null() {
        assertFalse(portfolio.removeShare(null));
    }

    @DisplayName("getShares: returns all shares")
    @Test
    void testGetShares() {
        portfolio.addShare(share);
        Stock stock2 = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        Share share2 = new Share(stock2, new BigDecimal("5"), new BigDecimal("298.00"));
        portfolio.addShare(share2);

        List<Share> shares = portfolio.getShares();
        assertEquals(2, shares.size());
    }

    @DisplayName("getShares: returns empty list for non-existent symbol")
    @Test
    void testGetShares_bySymbol_notFound() {
        portfolio.addShare(share);
        List<Share> shares = portfolio.getShares("MSFT");
        assertTrue(shares.isEmpty());
    }

    @DisplayName("getShares: filters shares by symbol")
    @Test
    void testGetShares_bySymbol_found() {
        portfolio.addShare(share);
        Stock stock2 = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        Share share2 = new Share(stock2, new BigDecimal("5"), new BigDecimal("298.00"));
        portfolio.addShare(share2);

        List<Share> aaplShares = portfolio.getShares("AAPL");
        assertEquals(1, aaplShares.size());
        assertEquals("AAPL", aaplShares.get(0).getStock().getSymbol());
    }

    @DisplayName("contains: returns true when share exists")
    @Test
    void testContains_true() {
        portfolio.addShare(share);
        assertTrue(portfolio.contains(share));
    }

    @DisplayName("contains: returns false when share doesn't exist")
    @Test
    void testContains_false() {
        assertFalse(portfolio.contains(share));
    }

    @DisplayName("getNetWorth: calculates total value correctly")
    @Test
    void testGetNetWorth() {
        // gross: 1500.00, commission: 15.00, profit after cost: 5.00, tax: 1.50
        portfolio.addShare(share);
        BigDecimal netWorth = portfolio.getNetWorth();
        assertEquals(0, netWorth.compareTo(new BigDecimal("1483.50")));
    }

    @DisplayName("getNetWorth: returns zero for empty portfolio")
    @Test
    void testGetNetWorth_empty() {
        assertEquals(0, portfolio.getNetWorth().compareTo(BigDecimal.ZERO));
    }
}
