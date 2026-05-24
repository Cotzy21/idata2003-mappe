package no.ntnu.idatx2003.millions.model;

import no.ntnu.idatx2003.millions.exception.StockNotFoundException;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.model.transaction.TransactionFactory;
import no.ntnu.idatx2003.millions.observer.Observable;
import no.ntnu.idatx2003.millions.observer.Observer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a stock exchange.
 * Manages available stocks, week tracking, and price updates.
 */
public class Exchange implements Observable<Exchange> {
    private final String name;
    private int week;
    private final Map<String, Stock> stockMap;
    private final Random random;
    private final List<Observer<Exchange>> observers;

    /**
     * Constructs an Exchange with initial stocks.
     *
     * @param name the name of the exchange
     * @param stocks the initial list of stocks
     */
    public Exchange(String name, List<Stock> stocks) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Exchange name cannot be null or empty");
        }
        if (stocks == null) {
            throw new IllegalArgumentException("Stocks list cannot be null");
        }

        this.name = name;
        this.week = 1;
        this.stockMap = new HashMap<>();
        this.random = new Random();
        this.observers = new ArrayList<>();

        for (Stock stock : stocks) {
            if (stock != null) {
                stockMap.put(stock.getSymbol(), stock);
            }
        }
    }

    /**
     * Gets the exchange name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current week.
     *
     * @return the week number
     */
    public int getWeek() {
        return week;
    }

    /**
     * Checks if a stock exists in the exchange.
     *
     * @param symbol the stock symbol
     * @return true if the stock exists
     */
    public boolean hasStock(String symbol) {
        return symbol != null && stockMap.containsKey(symbol);
    }

    /**
     * Gets a stock by symbol.
     *
     * @param symbol the stock symbol
     * @return the Stock
     * @throws StockNotFoundException if the stock doesn't exist
     */
    public Stock getStock(String symbol) throws StockNotFoundException {
        if (!hasStock(symbol)) {
            throw new StockNotFoundException("Stock not found: " + symbol);
        }
        return stockMap.get(symbol);
    }

    /**
     * Finds stocks by search term (matches symbol and company name, case-insensitive).
     *
     * @param searchTerm the term to search for
     * @return a list of matching stocks
     */
    public List<Stock> findStocks(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return new ArrayList<>(stockMap.values());
        }

        String lowerTerm = searchTerm.toLowerCase();
        return stockMap.values().stream()
                .filter(stock -> stock.getSymbol().toLowerCase().contains(lowerTerm) ||
                        stock.getCompany().toLowerCase().contains(lowerTerm))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Purchases shares from the exchange.
     *
     * @param symbol the stock symbol
     * @param quantity the number of shares to buy
     * @param player the player making the purchase
     * @return the completed Purchase transaction
     * @throws StockNotFoundException if the stock doesn't exist
     * @throws Exception if the purchase fails
     */
    public Transaction buy(String symbol, BigDecimal quantity, Player player)
            throws StockNotFoundException, Exception {
        Stock stock = getStock(symbol);
        Share share = new Share(stock, quantity, stock.getSalesPrice());
        Transaction purchase = TransactionFactory.createPurchase(share, week);
        purchase.commit(player);
        return purchase;
    }

    /**
     * Sells shares to the exchange.
     *
     * @param share the Share to sell
     * @param player the player making the sale
     * @return the completed Sale transaction
     * @throws Exception if the sale fails
     */
    public Transaction sell(Share share, Player player) throws Exception {
        Transaction sale = TransactionFactory.createSale(share, week);
        sale.commit(player);
        return sale;
    }

    /**
     * Sells a specified quantity from an existing portfolio share.
     *
     * @param share the portfolio Share to sell from
     * @param quantity the quantity to sell
     * @param player the player making the sale
     * @return the completed Sale transaction
     * @throws Exception if the sale fails
     */
    public Transaction sell(Share share, BigDecimal quantity, Player player) throws Exception {
        Transaction sale = TransactionFactory.createSale(share, quantity, week);
        sale.commit(player);
        return sale;
    }

    /**
     * Advances to the next week and updates all stock prices.
     * Prices change by a random amount between -10% and +10%.
     * Prices never become negative.
     */
    public void advance() {
        week++;
        stockMap.values().forEach(this::updatePriceFor);
        notifyObservers();
    }

    /**
     * Adds an observer that is notified when the exchange changes.
     *
     * @param observer the observer to add; must not be {@code null}
     * @return {@code true} if the observer was added
     */
    @Override
    public boolean addObserver(Observer<Exchange> observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        return observers.add(observer);
    }

    /**
     * Removes an observer.
     *
     * @param observer the observer to remove; must not be {@code null}
     * @return {@code true} if the observer was removed
     */
    @Override
    public boolean removeObserver(Observer<Exchange> observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        return observers.remove(observer);
    }

    /**
     * Notifies all registered observers about an exchange update.
     */
    @Override
    public void notifyObservers() {
        List.copyOf(observers).forEach(observer -> observer.update(this));
    }

    private void updatePriceFor(Stock stock) {
        BigDecimal currentPrice = stock.getSalesPrice();
        int basisPoints = random.nextInt(2001) - 1000;
        BigDecimal multiplier = BigDecimal.ONE.add(
                BigDecimal.valueOf(basisPoints).movePointLeft(4));

        BigDecimal newPrice = currentPrice.multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);

        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            newPrice = BigDecimal.ZERO;
        }
        stock.addNewSalesPrice(newPrice);
    }

    /**
     * Gets the top-performing stocks (gainers) up to a limit.
     * Sorted by latest price change in descending order.
     *
     * @param limit the maximum number of stocks to return
     * @return a list of top gainers
     */
    public List<Stock> getGainers(int limit) {
        return stockMap.values().stream()
                .sorted((s1, s2) -> s2.getLatestPriceChange().compareTo(s1.getLatestPriceChange()))
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets the worst-performing stocks (losers) up to a limit.
     * Sorted by latest price change in ascending order.
     *
     * @param limit the maximum number of stocks to return
     * @return a list of top losers
     */
    public List<Stock> getLosers(int limit) {
        return stockMap.values().stream()
                .sorted((s1, s2) -> s1.getLatestPriceChange().compareTo(s2.getLatestPriceChange()))
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all stocks in the exchange.
     *
     * @return an unmodifiable list of all stocks
     */
    public List<Stock> getAllStocks() {
        return List.copyOf(stockMap.values());
    }

    @Override
    public String toString() {
        return String.format("%s - Week %d, %d stocks", name, week, stockMap.size());
    }
}
