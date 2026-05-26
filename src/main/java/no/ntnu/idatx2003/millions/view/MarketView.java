package no.ntnu.idatx2003.millions.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.StockSearch;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Displays exchange stocks and trade controls.
 */
public class MarketView {
    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

    private final ObservableList<Stock> stockRows = FXCollections.observableArrayList();
    private final FilteredList<Stock> filteredStockRows = new FilteredList<>(stockRows);
    private final VBox root;
    private final TableView<Stock> stockTable;
    private final TextField searchField;
    private final TextField quantityField;
    private final Label marketStatsLabel;
    private final Label selectedStockLabel;
    private MainView.Actions actions;

    /**
     * Creates the market view.
     */
    public MarketView() {
        searchField = new TextField();
        stockTable = createStockTable();
        quantityField = new TextField("1");
        marketStatsLabel = mutedLabel();
        selectedStockLabel = mutedLabel("Select a stock to buy.");

        configureSearchField();

        root = new VBox(10, sectionTitle("Exchange"), searchField, stockTable, createTradeControls());
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
     * Moves keyboard focus to the quantity field.
     */
    public void requestQuantityFocus() {
        quantityField.requestFocus();
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
        TableView<Stock> table = new TableView<>(filteredStockRows);
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

    private void configureSearchField() {
        searchField.setPromptText("Søk symbol eller selskap");
        searchField.setTooltip(new Tooltip("Filtrer aksjer etter symbol eller selskap"));
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredStockRows.setPredicate(stock -> StockSearch.matches(stock, newValue)));
    }

    private VBox createTradeControls() {
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(120);
        quantityField.setMinWidth(96);
        quantityField.setTextFormatter(new TextFormatter<>(decimalFilter()));
        quantityField.setTooltip(new Tooltip("Antall aksjer (desimaltall er tillatt)"));

        BooleanBinding invalidQuantity = Bindings.createBooleanBinding(
                () -> !isValidQuantity(quantityField.getText()), quantityField.textProperty());
        quantityField.pseudoClassStateChanged(INVALID, invalidQuantity.get());
        invalidQuantity.addListener((observable, oldValue, invalid) ->
                quantityField.pseudoClassStateChanged(INVALID, invalid));

        Button buyButton = actionButton("_Buy", MainView.Actions::onBuySelectedStock);
        buyButton.setMnemonicParsing(true);
        buyButton.setTooltip(new Tooltip("Kjøp valgt aksje (Ctrl+B)"));
        buyButton.setDefaultButton(true);
        Button sellButton = actionButton("_Sell", MainView.Actions::onSellSelectedShare);
        sellButton.setMnemonicParsing(true);
        sellButton.setTooltip(new Tooltip("Selg valgt portefoljerad (Ctrl+S)"));
        buyButton.disableProperty().bind(invalidQuantity);
        sellButton.disableProperty().bind(invalidQuantity);

        Button nextWeekButton = actionButton("_Next week", MainView.Actions::onAdvanceWeek);
        nextWeekButton.setMnemonicParsing(true);
        nextWeekButton.setTooltip(new Tooltip("Gå til neste uke (Ctrl+N)"));
        Button loadCsvButton = actionButton("Load CSV", MainView.Actions::onLoadStocks);
        loadCsvButton.setTooltip(new Tooltip("Last inn aksjer fra CSV (Ctrl+O)"));
        Button saveCsvButton = actionButton("Save CSV", MainView.Actions::onSaveStocks);
        saveCsvButton.setTooltip(new Tooltip("Lagre aksjer til CSV (Ctrl+Shift+S)"));
        Button resetButton = actionButton("New game", MainView.Actions::onNewGame);
        resetButton.setTooltip(new Tooltip("Start et nytt spill"));

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

    private static UnaryOperator<TextFormatter.Change> decimalFilter() {
        return change -> {
            String text = change.getControlNewText();
            return text.matches("\\d*(\\.\\d*)?") ? change : null;
        };
    }

    private static boolean isValidQuantity(String text) {
        if (text == null || text.isBlank() || ".".equals(text)) {
            return false;
        }
        try {
            return new java.math.BigDecimal(text).compareTo(java.math.BigDecimal.ZERO) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
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
