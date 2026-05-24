package no.ntnu.idatx2003.millions.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.SplitPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Composes the main JavaFX screen from smaller view components.
 */
public class MainView {
    private final HeaderView headerView;
    private final MarketView marketView;
    private final PortfolioView portfolioView;
    private final StatusBar statusBar;
    private final BorderPane root;

    /**
     * Creates the main view.
     */
    public MainView() {
        headerView = new HeaderView();
        marketView = new MarketView();
        portfolioView = new PortfolioView();
        statusBar = new StatusBar();

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(headerView.getRoot());
        SplitPane content = new SplitPane(marketView.getRoot(), portfolioView.getRoot());
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
        portfolioView.setTransactions(transactions);
    }

    /**
     * Updates the summary metrics shown in the header.
     *
     * @param week the current week
     * @param cash the player's cash balance
     * @param portfolioValue the current portfolio value
     * @param netWorth the player's net worth
     * @param status the player's status
     */
    public void setSummary(int week, BigDecimal cash, BigDecimal portfolioValue,
                           BigDecimal netWorth, String status) {
        headerView.update(week, cash, portfolioValue, netWorth, status);
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
     * Opens a prompt for starting money.
     *
     * @param currentAmount the current starting amount
     * @return the entered amount text, or empty if cancelled
     */
    public Optional<String> askStartingMoney(BigDecimal currentAmount) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(
                ViewFormat.money(currentAmount));
        dialog.setTitle("New game");
        dialog.setHeaderText("Start a new game");
        dialog.setContentText("Starting money:");
        return dialog.showAndWait();
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

    private Window getWindow() {
        return root.getScene() == null ? null : root.getScene().getWindow();
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
