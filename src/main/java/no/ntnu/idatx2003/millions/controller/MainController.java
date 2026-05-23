package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.io.CsvStockReader;
import no.ntnu.idatx2003.millions.io.CsvStockWriter;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.io.StockDataWriter;
import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.SaleCalculator;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.view.MainView;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates user actions between {@link MainView} and the game model.
 */
public class MainController implements MainView.Actions {
    private static final BigDecimal DEFAULT_STARTING_MONEY = new BigDecimal("100000.00");

    private final MainView view;
    private final StockDataReader stockDataReader;
    private final StockDataWriter stockDataWriter;
    private BigDecimal startingMoney = DEFAULT_STARTING_MONEY;
    private Exchange exchange;
    private Player player;

    /**
     * Creates the main controller.
     *
     * @param view the view to control; must not be {@code null}
     */
    public MainController(MainView view) {
        this(view, new CsvStockReader(), new CsvStockWriter());
    }

    /**
     * Creates the main controller with explicit IO strategies.
     *
     * @param view the view to control; must not be {@code null}
     * @param stockDataReader the reader strategy to use; must not be {@code null}
     * @param stockDataWriter the writer strategy to use; must not be {@code null}
     */
    public MainController(MainView view, StockDataReader stockDataReader, StockDataWriter stockDataWriter) {
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.stockDataReader = Objects.requireNonNull(stockDataReader, "stockDataReader must not be null");
        this.stockDataWriter = Objects.requireNonNull(stockDataWriter, "stockDataWriter must not be null");
        this.view.setActions(this);
    }

    /**
     * Initializes a new default game and refreshes the view.
     */
    public void initialize() {
        resetGame(createDefaultStocks(), "Ready.");
    }

    /**
     * Handles buying the selected stock.
     */
    @Override
    public void onBuySelectedStock() {
        Stock stock = view.getSelectedStock();
        if (stock == null) {
            view.showMessage("Select a stock before buying.", true);
            return;
        }

        BigDecimal quantity = parseQuantity();
        if (quantity == null) {
            return;
        }

        try {
            exchange.buy(stock.getSymbol(), quantity, player);
            view.showMessage("Bought " + formatQuantity(quantity) + " " + stock.getSymbol() + ".", false);
            refreshAll();
        } catch (Exception exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Handles selling the selected portfolio share.
     */
    @Override
    public void onSellSelectedShare() {
        Share share = view.getSelectedShare();
        if (share == null) {
            view.showMessage("Select a portfolio row before selling.", true);
            return;
        }

        BigDecimal quantity = parseQuantity();
        if (quantity == null) {
            return;
        }

        try {
            exchange.sell(share, quantity, player);
            view.showMessage("Sold " + formatQuantity(quantity) + " "
                    + share.getStock().getSymbol() + ".", false);
            refreshAll();
        } catch (Exception exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Handles advancing the game by one week.
     */
    @Override
    public void onAdvanceWeek() {
        exchange.advance();
        view.showMessage("Advanced to week " + exchange.getWeek() + ".", false);
        refreshAll();
    }

    /**
     * Handles loading stocks from CSV.
     */
    @Override
    public void onLoadStocks() {
        File file = view.chooseCsvFileToLoad();
        if (file == null) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<Stock> stocks = stockDataReader.read(reader);
            if (stocks.isEmpty()) {
                view.showMessage("CSV file contains no stocks.", true);
                return;
            }
            resetGame(stocks, "Loaded " + stocks.size() + " stocks from " + file.getName() + ".");
        } catch (IOException | InvalidStockDataException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Handles saving stocks to CSV.
     */
    @Override
    public void onSaveStocks() {
        File file = view.chooseCsvFileToSave("stocks-week-" + exchange.getWeek() + ".csv");
        if (file == null) {
            return;
        }

        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            stockDataWriter.write(writer, exchange.getAllStocks());
            view.showMessage("Saved stocks to " + file.getName() + ".", false);
        } catch (IOException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Handles starting a new game.
     */
    @Override
    public void onNewGame() {
        view.askStartingMoney(startingMoney).ifPresent(this::startNewGame);
    }

    /**
     * Handles selected stock changes.
     *
     * @param stock the selected stock, or {@code null}
     */
    @Override
    public void onStockSelected(Stock stock) {
        if (stock == null) {
            view.clearSelectedStockDetails();
            return;
        }
        view.setSelectedStockDetails(stock.getSymbol() + " " + stock.getCompany()
                + " | price " + formatMoney(stock.getSalesPrice())
                + " | latest change " + formatSignedMoney(stock.getLatestPriceChange())
                + " | high " + formatMoney(stock.getHighestPrice())
                + " | low " + formatMoney(stock.getLowestPrice()), createPricePoints(stock));
    }

    /**
     * Handles selected portfolio share changes.
     *
     * @param share the selected share, or {@code null}
     */
    @Override
    public void onShareSelected(Share share) {
        if (share == null) {
            view.clearSelectedShareDetails();
            return;
        }
        view.setSelectedShareDetails("Holding " + formatQuantity(share.getQuantity()) + " "
                + share.getStock().getSymbol() + " | estimated sale proceeds "
                + formatMoney(new SaleCalculator(share).calculateTotal()));
    }

    private void startNewGame(String rawStartingMoney) {
        try {
            BigDecimal newStartingMoney = new BigDecimal(rawStartingMoney.trim());
            if (newStartingMoney.compareTo(BigDecimal.ZERO) < 0) {
                view.showMessage("Starting money must be non-negative.", true);
                return;
            }
            startingMoney = newStartingMoney;
            resetGame(createDefaultStocks(), "New game started with " + formatMoney(startingMoney) + " cash.");
        } catch (NumberFormatException exception) {
            view.showMessage("Starting money must be a valid number.", true);
        }
    }

    private void resetGame(List<Stock> stocks, String message) {
        exchange = new Exchange("Millions Exchange", stocks);
        player = new Player("Player", startingMoney);
        view.clearSelections();
        refreshAll();
        view.showMessage(message, false);
    }

    private BigDecimal parseQuantity() {
        String rawQuantity = view.getQuantityText();
        if (rawQuantity == null || rawQuantity.isBlank()) {
            view.showMessage("Quantity must be filled in.", true);
            return null;
        }

        try {
            BigDecimal quantity = new BigDecimal(rawQuantity.trim());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                view.showMessage("Quantity must be greater than zero.", true);
                return null;
            }
            return quantity;
        } catch (NumberFormatException exception) {
            view.showMessage("Quantity must be a valid number.", true);
            return null;
        }
    }

    private void refreshAll() {
        view.setStocks(exchange.getAllStocks().stream()
                .sorted(Comparator.comparing(Stock::getSymbol))
                .toList());
        view.setShares(player.getPortfolio().getShares());
        view.setTransactions(getSortedTransactions());
        view.setSummary(exchange.getWeek(), player.getMoney(), player.getPortfolio().getNetWorth(),
                player.getNetWorth(), player.getStatus().name());
        updateMarketStats();
        onStockSelected(view.getSelectedStock());
        onShareSelected(view.getSelectedShare());
    }

    private List<Transaction> getSortedTransactions() {
        List<Transaction> transactions = new ArrayList<>(player.getTransactionArchive().getTransactions());
        transactions.sort(Comparator.comparingInt(Transaction::getWeek).reversed());
        return List.copyOf(transactions);
    }

    private void updateMarketStats() {
        List<Stock> gainers = exchange.getGainers(1);
        List<Stock> losers = exchange.getLosers(1);
        if (gainers.isEmpty() || losers.isEmpty()) {
            view.setMarketStats("No market statistics available.");
            return;
        }
        Stock gainer = gainers.getFirst();
        Stock loser = losers.getFirst();
        view.setMarketStats("Top gainer: " + gainer.getSymbol() + " "
                + formatSignedMoney(gainer.getLatestPriceChange())
                + " | Top loser: " + loser.getSymbol() + " "
                + formatSignedMoney(loser.getLatestPriceChange()));
    }

    private List<MainView.PricePoint> createPricePoints(Stock stock) {
        List<BigDecimal> prices = stock.getHistoricalPrices();
        List<MainView.PricePoint> points = new ArrayList<>();
        for (int index = 0; index < prices.size(); index++) {
            BigDecimal previous = index == 0 ? prices.get(index) : prices.get(index - 1);
            points.add(new MainView.PricePoint(index + 1, prices.get(index), prices.get(index).subtract(previous)));
        }
        return points.reversed();
    }

    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String formatSignedMoney(BigDecimal amount) {
        String value = formatMoney(amount);
        return amount.compareTo(BigDecimal.ZERO) > 0 ? "+" + value : value;
    }

    private static String formatQuantity(BigDecimal quantity) {
        return quantity.stripTrailingZeros().toPlainString();
    }

    private static List<Stock> createDefaultStocks() {
        return List.of(
                new Stock("AKRBP", "Aker BP", new BigDecimal("246.80")),
                new Stock("DNB", "DNB Bank", new BigDecimal("228.40")),
                new Stock("EQNR", "Equinor", new BigDecimal("289.35")),
                new Stock("KOG", "Kongsberg Gruppen", new BigDecimal("1184.00")),
                new Stock("MOWI", "Mowi", new BigDecimal("187.55")),
                new Stock("NHY", "Norsk Hydro", new BigDecimal("68.42")),
                new Stock("ORK", "Orkla", new BigDecimal("102.15")),
                new Stock("TEL", "Telenor", new BigDecimal("133.90")),
                new Stock("TOM", "Tomra Systems", new BigDecimal("142.20")),
                new Stock("YAR", "Yara International", new BigDecimal("337.70"))
        );
    }
}
