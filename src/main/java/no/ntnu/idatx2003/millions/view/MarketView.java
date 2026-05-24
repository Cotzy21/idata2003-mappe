package no.ntnu.idatx2003.millions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2003.millions.model.Stock;

import java.util.List;

/**
 * Displays exchange stocks and trade controls.
 */
public class MarketView {
    private final ObservableList<Stock> stockRows = FXCollections.observableArrayList();
    private final VBox root;
    private final TableView<Stock> stockTable;
    private final TextField quantityField;
    private final Label marketStatsLabel;
    private final Label selectedStockLabel;
    private MainView.Actions actions;

    /**
     * Creates the market view.
     */
    public MarketView() {
        stockTable = createStockTable();
        quantityField = new TextField("1");
        marketStatsLabel = mutedLabel();
        selectedStockLabel = mutedLabel("Select a stock to buy.");

        root = new VBox(10, sectionTitle("Exchange"), stockTable, createTradeControls());
        root.getStyleClass().add("section-pane");
        root.setPadding(new Insets(18));
        VBox.setVgrow(stockTable, Priority.ALWAYS);
    }

    /**
     * Sets controller callbacks.
     *
     * @param actions the callbacks to use
     */
    public void setActions(MainView.Actions actions) {
        this.actions = actions;
    }

    /**
     * Returns this view's root node.
     *
     * @return the root node
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Returns the selected stock.
     *
     * @return the selected stock, or {@code null}
     */
    public Stock getSelectedStock() {
        return stockTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the raw quantity text.
     *
     * @return the quantity text
     */
    public String getQuantityText() {
        return quantityField.getText();
    }

    /**
     * Replaces stock rows.
     *
     * @param stocks the stocks to show
     */
    public void setStocks(List<Stock> stocks) {
        stockRows.setAll(stocks);
    }

    /**
     * Updates market statistics.
     *
     * @param text the statistics text
     */
    public void setMarketStats(String text) {
        marketStatsLabel.setText(text);
    }

    /**
     * Updates selected stock details.
     *
     * @param text the selected stock text
     */
    public void setSelectedStockDetails(String text) {
        selectedStockLabel.setText(text);
    }

    /**
     * Clears selected stock details.
     */
    public void clearSelectedStockDetails() {
        selectedStockLabel.setText("Select a stock to buy.");
    }

    /**
     * Clears table selection.
     */
    public void clearSelection() {
        stockTable.getSelectionModel().clearSelection();
    }

    private TableView<Stock> createStockTable() {
        TableView<Stock> table = new TableView<>(stockRows);
        table.getStyleClass().add("market-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No stocks available"));

        TableColumn<Stock, String> symbol = TableColumns.column("Symbol", Stock::getSymbol, 0.12);
        TableColumn<Stock, String> company = TableColumns.column("Company", Stock::getCompany, 0.34);
        TableColumn<Stock, String> price = TableColumns.column("Price",
                stock -> ViewFormat.money(stock.getSalesPrice()), 0.16);
        TableColumn<Stock, String> change = TableColumns.column("Change",
                stock -> ViewFormat.signedMoney(stock.getLatestPriceChange()), 0.14);
        TableColumn<Stock, String> high = TableColumns.column("High",
                stock -> ViewFormat.money(stock.getHighestPrice()), 0.12);
        TableColumn<Stock, String> low = TableColumns.column("Low",
                stock -> ViewFormat.money(stock.getLowestPrice()), 0.12);

        table.getColumns().setAll(List.of(symbol, company, price, change, high, low));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldStock, newStock) -> {
            if (actions != null) {
                actions.onStockSelected(newStock);
            }
        });
        return table;
    }

    private VBox createTradeControls() {
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(120);
        quantityField.setMinWidth(96);

        Button buyButton = actionButton("Buy selected", MainView.Actions::onBuySelectedStock);
        buyButton.setDefaultButton(true);
        Button sellButton = actionButton("Sell quantity", MainView.Actions::onSellSelectedShare);
        Button nextWeekButton = actionButton("Next week", MainView.Actions::onAdvanceWeek);
        Button loadCsvButton = actionButton("Load CSV", MainView.Actions::onLoadStocks);
        Button saveCsvButton = actionButton("Save CSV", MainView.Actions::onSaveStocks);
        Button resetButton = actionButton("New game", MainView.Actions::onNewGame);

        Label quantityLabel = new Label("Quantity");
        quantityLabel.setMinWidth(Region.USE_PREF_SIZE);

        FlowPane actionsBox = new FlowPane(10, 10, quantityLabel, quantityField, buyButton, sellButton,
                nextWeekButton, loadCsvButton, saveCsvButton, resetButton);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        actionsBox.getStyleClass().add("trade-actions");

        VBox box = new VBox(10, new Separator(), marketStatsLabel, selectedStockLabel, actionsBox);
        box.getStyleClass().add("trade-panel");
        box.setPadding(new Insets(8, 0, 0, 0));
        return box;
    }

    private Button actionButton(String text, ActionRunner runner) {
        Button button = new Button(text);
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setOnAction(event -> {
            if (actions != null) {
                runner.run(actions);
            }
        });
        return button;
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private static Label mutedLabel() {
        return mutedLabel("");
    }

    private static Label mutedLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted-label");
        label.setWrapText(true);
        return label;
    }

    @FunctionalInterface
    private interface ActionRunner {
        void run(MainView.Actions actions);
    }
}
