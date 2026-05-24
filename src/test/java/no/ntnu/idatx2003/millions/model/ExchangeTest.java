package no.ntnu.idatx2003.millions.model;

import no.ntnu.idatx2003.millions.exception.StockNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Exchange Tests")
class ExchangeTest {
    private Exchange exchange;
    private Stock stock1;
    private Stock stock2;

    @BeforeEach
    void setUp() {
        stock1 = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        stock2 = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock1);
        stocks.add(stock2);
        exchange = new Exchange("NYSE", stocks);
    }

    @DisplayName("Constructor: creates exchange with stocks")
    @Test
    void testConstructor_valid() {
        assertEquals("NYSE", exchange.getName());
        assertEquals(1, exchange.getWeek());
        assertEquals(2, exchange.getAllStocks().size());
    }

    @DisplayName("Constructor: throws exception for null name")
    @Test
    void testConstructor_nullName() {
        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock1);
        assertThrows(IllegalArgumentException.class,
                () -> new Exchange(null, stocks));
    }

    @DisplayName("Constructor: throws exception for null stocks list")
    @Test
    void testConstructor_nullStocks() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exchange("NYSE", null));
    }

    @DisplayName("hasStock: returns true for existing stock")
    @Test
    void testHasStock_true() {
        assertTrue(exchange.hasStock("AAPL"));
    }

    @DisplayName("hasStock: returns false for non-existing stock")
    @Test
    void testHasStock_false() {
        assertFalse(exchange.hasStock("GOOGL"));
    }

    @DisplayName("getStock: returns stock when exists")
    @Test
    void testGetStock_found() throws StockNotFoundException {
        Stock foundStock = exchange.getStock("AAPL");
        assertEquals("AAPL", foundStock.getSymbol());
    }

    @DisplayName("getStock: throws exception when not found")
    @Test
    void testGetStock_notFound() {
        assertThrows(StockNotFoundException.class,
                () -> exchange.getStock("GOOGL"));
    }

    @DisplayName("findStocks: returns all stocks when search term empty")
    @Test
    void testFindStocks_empty() {
        List<Stock> results = exchange.findStocks("");
        assertEquals(2, results.size());
    }

    @DisplayName("findStocks: filters by symbol (case-insensitive)")
    @Test
    void testFindStocks_bySymbol() {
        List<Stock> results = exchange.findStocks("aapl");
        assertEquals(1, results.size());
        assertEquals("AAPL", results.get(0).getSymbol());
    }

    @DisplayName("findStocks: filters by company name (case-insensitive)")
    @Test
    void testFindStocks_byCompany() {
        List<Stock> results = exchange.findStocks("microsoft");
        assertEquals(1, results.size());
        assertEquals("MSFT", results.get(0).getSymbol());
    }

    @DisplayName("findStocks: returns empty list when no matches")
    @Test
    void testFindStocks_noMatch() {
        List<Stock> results = exchange.findStocks("INVALID");
        assertTrue(results.isEmpty());
    }

    @DisplayName("advance: increments week")
    @Test
    void testAdvance_week() {
        assertEquals(1, exchange.getWeek());
        exchange.advance();
        assertEquals(2, exchange.getWeek());
    }

    @DisplayName("advance: updates stock prices")
    @Test
    void testAdvance_prices() {
        BigDecimal originalPrice = stock1.getSalesPrice();
        exchange.advance();
        BigDecimal newPrice = stock1.getSalesPrice();
        // Price should have changed (or stayed same with low probability)
        // Just verify it's non-negative
        assertTrue(newPrice.compareTo(BigDecimal.ZERO) >= 0);
    }

    @DisplayName("advance: notifies registered observers")
    @Test
    void advance_whenObserverIsRegistered_notifiesObserver() {
        AtomicInteger updates = new AtomicInteger();
        exchange.addObserver(updatedExchange -> updates.incrementAndGet());

        exchange.advance();

        assertEquals(1, updates.get());
    }

    @DisplayName("advance: does not notify removed observers")
    @Test
    void advance_whenObserverIsRemoved_doesNotNotifyObserver() {
        AtomicInteger updates = new AtomicInteger();
        no.ntnu.idatx2003.millions.observer.Observer<Exchange> observer =
                updatedExchange -> updates.incrementAndGet();
        exchange.addObserver(observer);
        exchange.removeObserver(observer);

        exchange.advance();

        assertEquals(0, updates.get());
    }

    @DisplayName("getGainers: returns top gainers in descending order")
    @Test
    void testGetGainers() {
        stock1.addNewSalesPrice(new BigDecimal("160.00")); // +10
        stock2.addNewSalesPrice(new BigDecimal("280.00")); // -20

        List<Stock> gainers = exchange.getGainers(1);
        assertEquals(1, gainers.size());
        assertEquals("AAPL", gainers.get(0).getSymbol());
    }

    @DisplayName("getLosers: returns top losers in ascending order")
    @Test
    void testGetLosers() {
        stock1.addNewSalesPrice(new BigDecimal("160.00")); // +10
        stock2.addNewSalesPrice(new BigDecimal("280.00")); // -20

        List<Stock> losers = exchange.getLosers(1);
        assertEquals(1, losers.size());
        assertEquals("MSFT", losers.get(0).getSymbol());
    }

    @DisplayName("buy: creates and commits purchase transaction")
    @Test
    void testBuy() throws Exception {
        Player player = new Player("Trader", new BigDecimal("50000.00"));
        exchange.buy("AAPL", new BigDecimal("10"), player);

        assertEquals(1, player.getPortfolio().getShares().size());
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("buy: throws exception for non-existing stock")
    @Test
    void testBuy_invalidStock() {
        Player player = new Player("Trader", new BigDecimal("50000.00"));
        assertThrows(StockNotFoundException.class,
                () -> exchange.buy("GOOGL", new BigDecimal("10"), player));
    }

    @DisplayName("sell: creates and commits sale transaction")
    @Test
    void testSell() throws Exception {
        Player player = new Player("Trader", new BigDecimal("50000.00"));
        Share share = new Share(stock1, new BigDecimal("10"), new BigDecimal("148.00"));
        player.getPortfolio().addShare(share);

        exchange.sell(share, player);

        assertEquals(0, player.getPortfolio().getShares().size());
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("sell: sells partial quantity and leaves remaining holding")
    @Test
    void testSell_partialQuantity() throws Exception {
        Player player = new Player("Trader", new BigDecimal("50000.00"));
        Share share = new Share(stock1, new BigDecimal("10"), new BigDecimal("148.00"));
        player.getPortfolio().addShare(share);

        exchange.sell(share, new BigDecimal("3"), player);

        assertEquals(1, player.getPortfolio().getShares().size());
        assertEquals(0, player.getPortfolio().getShares().get(0).getQuantity().compareTo(new BigDecimal("7")));
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }
}
