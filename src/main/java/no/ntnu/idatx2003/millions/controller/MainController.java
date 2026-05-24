package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.io.CsvStockReader;
import no.ntnu.idatx2003.millions.io.CsvStockWriter;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.io.StockDataWriter;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.view.MainView;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates the specialized controllers for the main Millions screen.
 */
public class MainController implements MainView.Actions {
    private static final BigDecimal DEFAULT_STARTING_MONEY = new BigDecimal("100000.00");

    private final MainView view;
    private final GameSession session;
    private final MarketController marketController;
    private final PortfolioController portfolioController;
    private final TransactionController transactionController;

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
        this.session = new GameSession(DEFAULT_STARTING_MONEY);
        this.transactionController = new TransactionController();
        this.marketController = new MarketController(
                view,
                session,
                Objects.requireNonNull(stockDataReader, "stockDataReader must not be null"),
                Objects.requireNonNull(stockDataWriter, "stockDataWriter must not be null"),
                this::refreshAll,
                this::resetGame);
        this.portfolioController = new PortfolioController(view, session, this::refreshAll);
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
        marketController.buySelectedStock();
    }

    /**
     * Handles selling the selected portfolio share.
     */
    @Override
    public void onSellSelectedShare() {
        portfolioController.sellSelectedShare();
    }

    /**
     * Handles advancing the game by one week.
     */
    @Override
    public void onAdvanceWeek() {
        marketController.advanceWeek();
    }

    /**
     * Handles loading stocks from CSV.
     */
    @Override
    public void onLoadStocks() {
        marketController.loadStocksFromCsv();
    }

    /**
     * Handles saving stocks to CSV.
     */
    @Override
    public void onSaveStocks() {
        marketController.saveStocksToCsv();
    }

    /**
     * Handles starting a new game.
     */
    @Override
    public void onNewGame() {
        view.askStartingMoney(session.startingMoney()).ifPresent(this::startNewGame);
    }

    /**
     * Handles selected stock changes.
     *
     * @param stock the selected stock, or {@code null}
     */
    @Override
    public void onStockSelected(Stock stock) {
        marketController.showStockDetails(stock);
    }

    /**
     * Handles selected portfolio share changes.
     *
     * @param share the selected share, or {@code null}
     */
    @Override
    public void onShareSelected(Share share) {
        portfolioController.showShareDetails(share);
    }

    private void startNewGame(String rawStartingMoney) {
        try {
            BigDecimal newStartingMoney = new BigDecimal(rawStartingMoney.trim());
            if (newStartingMoney.compareTo(BigDecimal.ZERO) < 0) {
                view.showMessage("Starting money must be non-negative.", true);
                return;
            }
            session.setStartingMoney(newStartingMoney);
            resetGame(createDefaultStocks(), "New game started with "
                    + ControllerFormat.money(session.startingMoney()) + " cash.");
        } catch (NumberFormatException exception) {
            view.showMessage("Starting money must be a valid number.", true);
        }
    }

    private void resetGame(List<Stock> stocks, String message) {
        session.reset(stocks);
        session.exchange().addObserver(exchange -> refreshAll());
        view.clearSelections();
        refreshAll();
        view.showMessage(message, false);
    }

    private void refreshAll() {
        view.setStocks(session.exchange().getAllStocks().stream()
                .sorted(Comparator.comparing(Stock::getSymbol))
                .toList());
        view.setShares(session.player().getPortfolio().getShares());
        view.setTransactions(transactionController.getTransactionsNewestFirst(session.player()));
        view.setSummary(session.exchange().getWeek(), session.player().getMoney(),
                session.player().getPortfolio().getNetWorth(), session.player().getNetWorth(),
                session.player().getStatus().name());
        marketController.updateMarketStats();
        marketController.showStockDetails(view.getSelectedStock());
        portfolioController.showShareDetails(view.getSelectedShare());
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
