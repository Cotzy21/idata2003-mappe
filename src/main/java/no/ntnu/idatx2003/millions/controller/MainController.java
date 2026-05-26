package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.io.CsvStockReader;
import no.ntnu.idatx2003.millions.io.CsvStockWriter;
import no.ntnu.idatx2003.millions.io.GameState;
import no.ntnu.idatx2003.millions.io.GameStateMapper;
import no.ntnu.idatx2003.millions.io.GameStateReader;
import no.ntnu.idatx2003.millions.io.GameStateWriter;
import no.ntnu.idatx2003.millions.io.JsonGameStateReader;
import no.ntnu.idatx2003.millions.io.JsonGameStateWriter;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.io.StockDataWriter;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.util.Validate;
import no.ntnu.idatx2003.millions.view.MainView;
import no.ntnu.idatx2003.millions.view.NewGameResult;

import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates the specialized controllers for the main Millions screen.
 */
public class MainController implements MainView.Actions {
    private static final BigDecimal DEFAULT_STARTING_MONEY = new BigDecimal("100000.00");

    private final MainView view;
    private final StockDataReader stockDataReader;
    private final GameStateReader gameStateReader;
    private final GameStateWriter gameStateWriter;
    private final GameSession session;
    private final MarketController marketController;
    private final PortfolioController portfolioController;
    private final TransactionController transactionController;

    /**
     * Creates the main controller using the default CSV strategies.
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
        this.view = Validate.requireNonNull(view, "view");
        this.stockDataReader = Validate.requireNonNull(stockDataReader, "stockDataReader");
        this.gameStateReader = new JsonGameStateReader();
        this.gameStateWriter = new JsonGameStateWriter();
        this.session = new GameSession(DEFAULT_STARTING_MONEY);
        this.transactionController = new TransactionController();
        this.marketController = new MarketController(
                view,
                session,
                stockDataReader,
                Validate.requireNonNull(stockDataWriter, "stockDataWriter"),
                this::refreshAll,
                this::resetGame);
        this.portfolioController = new PortfolioController(view, session, this::refreshAll);
        this.view.setActions(this);
    }

    /**
     * Shows the new-game dialog at startup and prepares the session from the
     * dialog result. If the user cancels, falls back to the built-in defaults
     * so the application is always in a playable state.
     */
    public void initialize() {
        Optional<NewGameResult> result = view.askNewGame(stockDataReader,
                MainController::createDefaultStocks,
                session.playerName(),
                session.startingMoney());
        if (result.isPresent()) {
            applyNewGame(result.get());
        } else {
            resetGame(createDefaultStocks(), "Ready.");
        }
    }

    @Override
    public void onBuySelectedStock() {
        marketController.buySelectedStock();
    }

    @Override
    public void onSellSelectedShare() {
        portfolioController.sellSelectedShare();
    }

    @Override
    public void onAdvanceWeek() {
        marketController.advanceWeek();
    }

    @Override
    public void onLoadStocks() {
        marketController.loadStocksFromCsv();
    }

    @Override
    public void onSaveStocks() {
        marketController.saveStocksToCsv();
    }

    @Override
    public void onNewGame() {
        view.askNewGame(stockDataReader,
                MainController::createDefaultStocks,
                session.playerName(),
                session.startingMoney())
                .ifPresent(this::applyNewGame);
    }

    @Override
    public void onShowMarketOverview() {
        view.showWinnersLosers(session.exchange());
    }

    @Override
    public void onLiquidateAndExit() {
        portfolioController.liquidateAndExit();
    }

    @Override
    public void onLoadGame() {
        File file = view.chooseGameFileToLoad();
        if (file == null) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            GameState gameState = gameStateReader.read(reader);
            GameStateMapper.MaterializedGameState materialized = GameStateMapper.fromGameState(gameState);
            session.load(materialized.player(), materialized.exchange());
            session.exchange().addObserver(exchange -> refreshAll());
            view.clearSelections();
            refreshAll();
            view.showMessage("Loaded game from " + file.getName() + ".", false);
        } catch (IOException | IllegalArgumentException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void onSaveGame() {
        File file = view.chooseGameFileToSave("millions-week-" + session.exchange().getWeek() + ".json");
        if (file == null) {
            return;
        }
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            gameStateWriter.write(writer, GameStateMapper.toGameState(session.player(), session.exchange()));
            view.showMessage("Saved game to " + file.getName() + ".", false);
        } catch (IOException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void onStockSelected(Stock stock) {
        marketController.showStockDetails(stock);
        if (stock != null) {
            view.requestQuantityFocus();
        }
    }

    @Override
    public void onShareSelected(Share share) {
        portfolioController.showShareDetails(share);
    }

    private void applyNewGame(NewGameResult result) {
        session.setPlayerName(result.playerName());
        session.setStartingMoney(result.startingMoney());
        String source = result.sourceFile() == null ? "default stocks" : result.sourceFile().getName();
        resetGame(result.stocks(), "New game for " + result.playerName()
                + " with " + ControllerFormat.money(result.startingMoney())
                + " cash (" + source + ").");
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
        view.setSummary(session.playerName(), session.exchange().getWeek(),
                session.player().getMoney(), session.player().getPortfolio().getNetWorth(),
                session.player().getNetWorth(), session.player().getStatus().name());
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
