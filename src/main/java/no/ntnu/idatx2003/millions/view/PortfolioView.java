package no.ntnu.idatx2003.millions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.transaction.SaleCalculator;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Displays portfolio holdings, stock history, and transaction history.
 */
public class PortfolioView {
    private final ObservableList<Share> shareRows = FXCollections.observableArrayList();
    private final ObservableList<Transaction> transactionRows = FXCollections.observableArrayList();
    private final ObservableList<MainView.PricePoint> priceHistoryRows = FXCollections.observableArrayList();
    private final VBox root;
    private final TableView<Share> portfolioTable;
    private final TableView<Transaction> transactionTable;
    private final LineChart<Number, Number> priceHistoryChart;
    private final Label selectedShareLabel;
    private MainView.Actions actions;

    /**
     * Creates the portfolio view.
     */
    public PortfolioView() {
        portfolioTable = createPortfolioTable();
        priceHistoryChart = createPriceHistoryChart();
        TableView<MainView.PricePoint> priceHistoryTable = createPriceHistoryTable();
        transactionTable = createTransactionTable();
        selectedShareLabel = mutedLabel("Select a portfolio row to sell.");

        root = new VBox(10, sectionTitle("Portfolio"), portfolioTable, selectedShareLabel,
                sectionTitle("Selected stock history"), priceHistoryChart, priceHistoryTable,
                sectionTitle("Transactions"), transactionTable);
        root.getStyleClass().add("section-pane");
        root.setPadding(new Insets(18));
        VBox.setVgrow(portfolioTable, Priority.ALWAYS);
        VBox.setVgrow(priceHistoryChart, Priority.ALWAYS);
        VBox.setVgrow(priceHistoryTable, Priority.ALWAYS);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
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
     * Returns the selected share.
     *
     * @return the selected share, or {@code null}
     */
    public Share getSelectedShare() {
        return portfolioTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Replaces share rows.
     *
     * @param shares the shares to show
     */
    public void setShares(List<Share> shares) {
        shareRows.setAll(shares);
    }

    /**
     * Replaces transaction rows.
     *
     * @param transactions the transactions to show
     */
    public void setTransactions(List<Transaction> transactions) {
        transactionRows.setAll(transactions);
    }

    /**
     * Replaces selected stock price history rows.
     *
     * @param pricePoints the price points to show
     */
    public void setPricePoints(List<MainView.PricePoint> pricePoints) {
        priceHistoryRows.setAll(pricePoints);
        priceHistoryChart.getData().setAll(List.of(createPriceSeries(pricePoints)));
    }

    /**
     * Updates selected share details.
     *
     * @param text the selected share text
     */
    public void setSelectedShareDetails(String text) {
        selectedShareLabel.setText(text);
    }

    /**
     * Clears selected share details.
     */
    public void clearSelectedShareDetails() {
        selectedShareLabel.setText("Select a portfolio row to sell.");
    }

    /**
     * Clears selected stock history rows.
     */
    public void clearPricePoints() {
        priceHistoryRows.clear();
        priceHistoryChart.getData().clear();
    }

    /**
     * Clears table selections.
     */
    public void clearSelections() {
        portfolioTable.getSelectionModel().clearSelection();
        transactionTable.getSelectionModel().clearSelection();
    }

    private TableView<Share> createPortfolioTable() {
        TableView<Share> table = new TableView<>(shareRows);
        table.getStyleClass().add("portfolio-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No shares in portfolio"));

        TableColumn<Share, String> symbol = TableColumns.column("Symbol", share -> share.getStock().getSymbol(), 0.16);
        TableColumn<Share, String> company = TableColumns.column("Company", share -> share.getStock().getCompany(), 0.30);
        TableColumn<Share, String> quantity = TableColumns.column("Qty",
                share -> ViewFormat.quantity(share.getQuantity()), 0.12);
        TableColumn<Share, String> purchase = TableColumns.column("Bought",
                share -> ViewFormat.money(share.getPurchasePrice()), 0.14);
        TableColumn<Share, String> current = TableColumns.column("Current",
                share -> ViewFormat.money(share.getStock().getSalesPrice()), 0.13);
        TableColumn<Share, String> saleValue = TableColumns.column("Sale value",
                share -> ViewFormat.money(new SaleCalculator(share).calculateTotal()), 0.13);
        TableColumn<Share, String> profitLoss = TableColumns.column("P/L",
                share -> ViewFormat.signedMoney(calculateProfitLoss(share)), 0.12);

        table.getColumns().setAll(List.of(symbol, company, quantity, purchase, current, saleValue, profitLoss));
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldShare, newShare) -> {
            if (actions != null) {
                actions.onShareSelected(newShare);
            }
        });
        return table;
    }

    private TableView<MainView.PricePoint> createPriceHistoryTable() {
        TableView<MainView.PricePoint> table = new TableView<>(priceHistoryRows);
        table.getStyleClass().add("history-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("Select a stock to see price history"));

        TableColumn<MainView.PricePoint, String> week = TableColumns.column("Week",
                point -> Integer.toString(point.week()), 0.20);
        TableColumn<MainView.PricePoint, String> price = TableColumns.column("Price",
                point -> ViewFormat.money(point.price()), 0.40);
        TableColumn<MainView.PricePoint, String> change = TableColumns.column("Change",
                point -> ViewFormat.signedMoney(point.change()), 0.40);

        table.getColumns().setAll(List.of(week, price, change));
        return table;
    }

    private LineChart<Number, Number> createPriceHistoryChart() {
        NumberAxis weekAxis = new NumberAxis();
        weekAxis.setLabel("Week");
        weekAxis.setForceZeroInRange(false);

        NumberAxis priceAxis = new NumberAxis();
        priceAxis.setLabel("Price");
        priceAxis.setForceZeroInRange(false);

        LineChart<Number, Number> chart = new LineChart<>(weekAxis, priceAxis);
        chart.getStyleClass().add("price-chart");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(180);
        return chart;
    }

    private XYChart.Series<Number, Number> createPriceSeries(List<MainView.PricePoint> pricePoints) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Price");
        List<XYChart.Data<Number, Number>> points = pricePoints.reversed().stream()
                .map(point -> new XYChart.Data<Number, Number>(point.week(), point.price()))
                .toList();
        series.getData().setAll(points);
        return series;
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>(transactionRows);
        table.getStyleClass().add("transaction-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No transactions yet"));

        TableColumn<Transaction, String> type = TableColumns.column("Type",
                transaction -> transaction.getClass().getSimpleName(), 0.16);
        TableColumn<Transaction, String> week = TableColumns.column("Week",
                transaction -> Integer.toString(transaction.getWeek()), 0.10);
        TableColumn<Transaction, String> symbol = TableColumns.column("Symbol",
                transaction -> transaction.getShare().getStock().getSymbol(), 0.14);
        TableColumn<Transaction, String> quantity = TableColumns.column("Qty",
                transaction -> ViewFormat.quantity(transaction.getShare().getQuantity()), 0.12);
        TableColumn<Transaction, String> gross = TableColumns.column("Gross",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateGross()), 0.16);
        TableColumn<Transaction, String> fees = TableColumns.column("Fees",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateCommission()
                        .add(transaction.getCalculator().calculateTax())), 0.16);
        TableColumn<Transaction, String> total = TableColumns.column("Total",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateTotal()), 0.16);

        table.getColumns().setAll(List.of(type, week, symbol, quantity, gross, fees, total));
        return table;
    }

    private static BigDecimal calculateProfitLoss(Share share) {
        BigDecimal saleValue = new SaleCalculator(share).calculateTotal();
        BigDecimal purchaseCost = share.getPurchasePrice().multiply(share.getQuantity());
        return saleValue.subtract(purchaseCost);
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private static Label mutedLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted-label");
        label.setWrapText(true);
        return label;
    }
}
