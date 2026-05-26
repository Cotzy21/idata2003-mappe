package no.ntnu.idatx2003.millions.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.model.transaction.TransactionCalculator;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Composes the main JavaFX screen from smaller view components.
 */
public class MainView {
    private final HeaderView headerView;
    private final MarketView marketView;
    private final PortfolioView portfolioView;
    private final TransactionsView transactionsView;
    private final StatusBar statusBar;
    private final BorderPane root;

    /**
     * Creates the main view.
     */
    public MainView() {
        headerView = new HeaderView();
        marketView = new MarketView();
        portfolioView = new PortfolioView();
        transactionsView = new TransactionsView();
        statusBar = new StatusBar();

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(headerView.getRoot());
        SplitPane content = new SplitPane(marketView.getRoot(), createRightTabs());
        content.setDividerPositions(0.52);
        root.setCenter(content);
        root.setBottom(statusBar.getRoot());
    }

    /**
     * Sets the controller callbacks used by this view.
     *
     * @param actions the actions to call on user input; must not be {@code null}
     */
    public void setActions(Actions actions) {
        java.util.Objects.requireNonNull(actions, "actions must not be null");
        headerView.setActions(actions);
        marketView.setActions(actions);
        portfolioView.setActions(actions);
    }

    /**
     * Returns the root node for this view.
     *
     * @return the root pane, never {@code null}
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Returns the currently selected stock.
     *
     * @return the selected stock, or {@code null} when no stock is selected
     */
    public Stock getSelectedStock() {
        return marketView.getSelectedStock();
    }

    /**
     * Returns the currently selected portfolio share.
     *
     * @return the selected share, or {@code null} when no share is selected
     */
    public Share getSelectedShare() {
        return portfolioView.getSelectedShare();
    }

    /**
     * Returns the raw quantity text entered by the user.
     *
     * @return the quantity text
     */
    public String getQuantityText() {
        return marketView.getQuantityText();
    }

    /**
     * Replaces the stock rows shown in the market table.
     *
     * @param stocks the stocks to show
     */
    public void setStocks(List<Stock> stocks) {
        marketView.setStocks(stocks);
    }

    /**
     * Replaces the portfolio rows shown in the portfolio table.
     *
     * @param shares the shares to show
     */
    public void setShares(List<Share> shares) {
        portfolioView.setShares(shares);
    }

    /**
     * Replaces the transaction rows shown in the transaction table.
     *
     * @param transactions the transactions to show
     */
    public void setTransactions(List<Transaction> transactions) {
        transactionsView.setTransactions(transactions);
    }

    /**
     * Updates the summary metrics shown in the header.
     *
     * @param playerName the player's display name
     * @param week the current week
     * @param cash the player's cash balance
     * @param portfolioValue the current portfolio value
     * @param netWorth the player's net worth
     * @param status the player's status
     */
    public void setSummary(String playerName, int week, BigDecimal cash, BigDecimal portfolioValue,
                           BigDecimal netWorth, String status) {
        headerView.update(playerName, week, cash, portfolioValue, netWorth, status);
    }

    /**
     * Updates the market statistics text.
     *
     * @param text the statistics text
     */
    public void setMarketStats(String text) {
        marketView.setMarketStats(text);
    }

    /**
     * Updates details for the selected stock.
     *
     * @param text the stock detail text
     * @param pricePoints the historical price rows to show
     */
    public void setSelectedStockDetails(String text, List<PricePoint> pricePoints) {
        marketView.setSelectedStockDetails(text);
        portfolioView.setPricePoints(pricePoints);
    }

    /**
     * Clears selected stock details.
     */
    public void clearSelectedStockDetails() {
        marketView.clearSelectedStockDetails();
        portfolioView.clearPricePoints();
    }

    /**
     * Updates details for the selected portfolio share.
     *
     * @param text the share detail text
     */
    public void setSelectedShareDetails(String text) {
        portfolioView.setSelectedShareDetails(text);
    }

    /**
     * Clears selected share details.
     */
    public void clearSelectedShareDetails() {
        portfolioView.clearSelectedShareDetails();
    }

    /**
     * Clears all table selections.
     */
    public void clearSelections() {
        marketView.clearSelection();
        portfolioView.clearSelections();
        transactionsView.clearSelection();
    }

    /**
     * Shows a status message.
     *
     * @param message the message to show
     * @param error whether the message represents an error
     */
    public void showMessage(String message, boolean error) {
        statusBar.showMessage(message, error);
    }

    /**
     * Opens the new-game configuration dialog (player name, starting capital
     * and stock data source).
     *
     * @param stockDataReader the reader strategy used by the file picker
     * @param defaultStocksSupplier supplier for the default stock list
     * @param suggestedName initial value for the name field
     * @param suggestedAmount initial value for the starting money field
     * @return the dialog result, or empty when the user cancelled
     */
    public Optional<NewGameResult> askNewGame(StockDataReader stockDataReader,
                                              Supplier<List<Stock>> defaultStocksSupplier,
                                              String suggestedName,
                                              BigDecimal suggestedAmount) {
        NewGameDialog dialog = new NewGameDialog(stockDataReader, defaultStocksSupplier,
                suggestedName, suggestedAmount);
        return dialog.showAndWait();
    }

    /**
     * Shows the transaction preview dialog and returns the user's choice.
     *
     * @param kind preview type (BUY/SELL)
     * @param stock stock involved in the trade
     * @param quantity quantity being traded
     * @param pricePerShare per-share price
     * @param calculator transaction calculator
     * @return {@code true} when the user confirmed, {@code false} otherwise
     */
    public boolean confirmTransaction(TransactionPreviewDialog.Kind kind, Stock stock,
                                      BigDecimal quantity, BigDecimal pricePerShare,
                                      TransactionCalculator calculator) {
        TransactionPreviewDialog dialog =
                new TransactionPreviewDialog(kind, stock, quantity, pricePerShare, calculator);
        return dialog.showAndWait().orElse(Boolean.FALSE);
    }

    /**
     * Shows a receipt dialog for a freshly committed transaction.
     *
     * @param transaction the committed transaction
     * @param player the affected player
     */
    public void showReceipt(Transaction transaction, Player player) {
        new TransactionReceiptDialog(transaction, player).showAndWait();
    }

    /**
     * Shows the market overview dialog with winners and losers.
     *
     * @param exchange the exchange to observe
     */
    public void showWinnersLosers(Exchange exchange) {
        new WinnersLosersDialog(exchange).show();
    }

    /**
     * Asks the user to confirm liquidation before all shares are sold.
     *
     * @param estimatedFinalWorth estimated final net worth after liquidation
     * @return {@code true} if the user confirms
     */
    public boolean confirmLiquidation(BigDecimal estimatedFinalWorth) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Selg alt og avslutt");
        alert.setHeaderText("Selg hele porteføljen og avslutt spillet?");
        alert.setContentText("Estimert sluttbeholdning: " + ViewFormat.money(estimatedFinalWorth));
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Shows the final receipt after liquidation.
     *
     * @param player the finished player state
     * @param week the final week
     * @param transactions sale transactions created by liquidation
     */
    public void showFinalReceipt(Player player, int week, List<Transaction> transactions) {
        new FinalReceiptDialog(player, week, transactions).showAndWait();
    }

    /**
     * Moves keyboard focus to the quantity field.
     */
    public void requestQuantityFocus() {
        marketView.requestQuantityFocus();
    }

    /**
     * Lets the user choose a CSV file to load.
     *
     * @return the chosen file, or {@code null} if cancelled
     */
    public File chooseCsvFileToLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load stocks from CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        return fileChooser.showOpenDialog(getWindow());
    }

    /**
     * Lets the user choose a CSV file to save.
     *
     * @param initialFileName the suggested file name
     * @return the chosen file, or {@code null} if cancelled
     */
    public File chooseCsvFileToSave(String initialFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save stocks to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fileChooser.setInitialFileName(initialFileName);
        return fileChooser.showSaveDialog(getWindow());
    }

    /**
     * Lets the user choose a JSON file to load a saved game.
     *
     * @return the chosen file, or {@code null} if cancelled
     */
    public File chooseGameFileToLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load game from JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        return fileChooser.showOpenDialog(getWindow());
    }

    /**
     * Lets the user choose a JSON file to save the active game.
     *
     * @param initialFileName the suggested file name
     * @return the chosen file, or {@code null} if cancelled
     */
    public File chooseGameFileToSave(String initialFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save game to JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fileChooser.setInitialFileName(initialFileName);
        return fileChooser.showSaveDialog(getWindow());
    }

    private Window getWindow() {
        return root.getScene() == null ? null : root.getScene().getWindow();
    }

    private TabPane createRightTabs() {
        TabPane tabs = new TabPane();
        Tab portfolioTab = new Tab("Portfolio", portfolioView.getRoot());
        Tab transactionsTab = new Tab("Transactions", transactionsView.getRoot());
        portfolioTab.setClosable(false);
        transactionsTab.setClosable(false);
        tabs.getTabs().setAll(portfolioTab, transactionsTab);
        return tabs;
    }

    /**
     * Price point shown in the selected stock history table.
     *
     * @param week the week number
     * @param price the stock price for the week
     * @param change the price change from the previous row
     */
    public record PricePoint(int week, BigDecimal price, BigDecimal change) {
    }

    /**
     * Controller callbacks used by the main view.
     */
    public interface Actions {

        /**
         * Handles a buy request for the selected stock.
         */
        void onBuySelectedStock();

        /**
         * Handles a sell request for the selected portfolio share.
         */
        void onSellSelectedShare();

        /**
         * Handles advancing the exchange to the next week.
         */
        void onAdvanceWeek();

        /**
         * Handles loading stocks from a CSV file.
         */
        void onLoadStocks();

        /**
         * Handles saving stocks to a CSV file.
         */
        void onSaveStocks();

        /**
         * Handles starting a new game.
         */
        void onNewGame();

        /**
         * Shows market winners and losers.
         */
        void onShowMarketOverview();

        /**
         * Sells the full portfolio and exits the application.
         */
        void onLiquidateAndExit();

        /**
         * Loads a saved game from JSON.
         */
        void onLoadGame();

        /**
         * Saves the active game to JSON.
         */
        void onSaveGame();

        /**
         * Handles stock selection changes.
         *
         * @param stock the selected stock, or {@code null}
         */
        void onStockSelected(Stock stock);

        /**
         * Handles portfolio share selection changes.
         *
         * @param share the selected share, or {@code null}
         */
        void onShareSelected(Share share);
    }
}
