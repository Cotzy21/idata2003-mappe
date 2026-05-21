package no.ntnu.idatx2003.millions;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.SaleCalculator;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * JavaFX Application class for the Millions stock trading game.
 */
public class App extends Application {
    private static final BigDecimal STARTING_MONEY = new BigDecimal("100000.00");

    private Exchange exchange;
    private Player player;

    private final ObservableList<Stock> stockRows = FXCollections.observableArrayList();
    private final ObservableList<Share> shareRows = FXCollections.observableArrayList();
    private final ObservableList<Transaction> transactionRows = FXCollections.observableArrayList();

    private TableView<Stock> stockTable;
    private TableView<Share> portfolioTable;
    private TableView<Transaction> transactionTable;

    private TextField quantityField;
    private Label weekLabel;
    private Label cashLabel;
    private Label portfolioValueLabel;
    private Label netWorthLabel;
    private Label statusLabel;
    private Label selectedStockLabel;
    private Label selectedShareLabel;
    private Label messageLabel;

    /**
     * Constructs the JavaFX application.
     */
    public App() {
        // Required by JavaFX.
    }

    /**
     * Starts the JavaFX application.
     *
     * @param stage the primary stage
     */
    @Override
    public void start(Stage stage) {
        this.exchange = new Exchange("Millions Exchange", createDefaultStocks());
        this.player = new Player("Player", STARTING_MONEY);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(createStatusLine());
        root.setStyle("-fx-font-family: 'System'; -fx-background-color: #f6f7f9;");

        refreshAll();

        stage.setTitle("Millions - Stock Trading Game");
        stage.setScene(new Scene(root, 1180, 760));
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.show();
    }

    private VBox createHeader() {
        Label title = new Label("Millions");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");

        Label subtitle = new Label("Stock trading game");
        subtitle.setTextFill(Color.web("#5d6673"));

        weekLabel = metricLabel();
        cashLabel = metricLabel();
        portfolioValueLabel = metricLabel();
        netWorthLabel = metricLabel();
        statusLabel = metricLabel();

        GridPane metrics = new GridPane();
        metrics.setHgap(18);
        metrics.setVgap(4);
        metrics.add(new Label("Week"), 0, 0);
        metrics.add(weekLabel, 0, 1);
        metrics.add(new Label("Cash"), 1, 0);
        metrics.add(cashLabel, 1, 1);
        metrics.add(new Label("Portfolio"), 2, 0);
        metrics.add(portfolioValueLabel, 2, 1);
        metrics.add(new Label("Net worth"), 3, 0);
        metrics.add(netWorthLabel, 3, 1);
        metrics.add(new Label("Status"), 4, 0);
        metrics.add(statusLabel, 4, 1);

        HBox header = new HBox(24, new VBox(2, title, subtitle), metrics);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 22, 16, 22));
        HBox.setHgrow(metrics, Priority.ALWAYS);

        return new VBox(header, new Separator());
    }

    private SplitPane createMainContent() {
        stockTable = createStockTable();
        portfolioTable = createPortfolioTable();
        transactionTable = createTransactionTable();

        VBox marketPane = new VBox(10, sectionTitle("Exchange"), stockTable, createTradeControls());
        marketPane.setPadding(new Insets(18));
        VBox.setVgrow(stockTable, Priority.ALWAYS);

        VBox portfolioPane = new VBox(10, sectionTitle("Portfolio"), portfolioTable,
                sectionTitle("Transactions"), transactionTable);
        portfolioPane.setPadding(new Insets(18));
        VBox.setVgrow(portfolioTable, Priority.ALWAYS);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(marketPane, portfolioPane);
        splitPane.setDividerPositions(0.52);
        return splitPane;
    }

    private TableView<Stock> createStockTable() {
        TableView<Stock> table = new TableView<>(stockRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No stocks available"));

        TableColumn<Stock, String> symbol = column("Symbol", stock -> stock.getSymbol(), 0.12);
        TableColumn<Stock, String> company = column("Company", stock -> stock.getCompany(), 0.34);
        TableColumn<Stock, String> price = column("Price", stock -> formatMoney(stock.getSalesPrice()), 0.16);
        TableColumn<Stock, String> change = column("Change", stock -> formatSignedMoney(stock.getLatestPriceChange()), 0.14);
        TableColumn<Stock, String> high = column("High", stock -> formatMoney(stock.getHighestPrice()), 0.12);
        TableColumn<Stock, String> low = column("Low", stock -> formatMoney(stock.getLowestPrice()), 0.12);

        table.getColumns().setAll(List.of(symbol, company, price, change, high, low));
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldStock, newStock) -> updateSelectedStock(newStock));
        return table;
    }

    private TableView<Share> createPortfolioTable() {
        TableView<Share> table = new TableView<>(shareRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No shares in portfolio"));

        TableColumn<Share, String> symbol = column("Symbol", share -> share.getStock().getSymbol(), 0.16);
        TableColumn<Share, String> company = column("Company", share -> share.getStock().getCompany(), 0.30);
        TableColumn<Share, String> quantity = column("Qty", share -> formatQuantity(share.getQuantity()), 0.12);
        TableColumn<Share, String> purchase = column("Bought", share -> formatMoney(share.getPurchasePrice()), 0.14);
        TableColumn<Share, String> current = column("Current", share -> formatMoney(share.getStock().getSalesPrice()), 0.14);
        TableColumn<Share, String> saleValue = column("Sale value",
                share -> formatMoney(new SaleCalculator(share).calculateTotal()), 0.14);

        table.getColumns().setAll(List.of(symbol, company, quantity, purchase, current, saleValue));
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldShare, newShare) -> updateSelectedShare(newShare));
        return table;
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>(transactionRows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No transactions yet"));

        TableColumn<Transaction, String> type = column("Type",
                transaction -> transaction.getClass().getSimpleName(), 0.16);
        TableColumn<Transaction, String> week = column("Week",
                transaction -> Integer.toString(transaction.getWeek()), 0.10);
        TableColumn<Transaction, String> symbol = column("Symbol",
                transaction -> transaction.getShare().getStock().getSymbol(), 0.14);
        TableColumn<Transaction, String> quantity = column("Qty",
                transaction -> formatQuantity(transaction.getShare().getQuantity()), 0.12);
        TableColumn<Transaction, String> gross = column("Gross",
                transaction -> formatMoney(transaction.getCalculator().calculateGross()), 0.16);
        TableColumn<Transaction, String> fees = column("Fees",
                transaction -> formatMoney(transaction.getCalculator().calculateCommission()
                        .add(transaction.getCalculator().calculateTax())), 0.16);
        TableColumn<Transaction, String> total = column("Total",
                transaction -> formatMoney(transaction.getCalculator().calculateTotal()), 0.16);

        table.getColumns().setAll(List.of(type, week, symbol, quantity, gross, fees, total));
        return table;
    }

    private VBox createTradeControls() {
        quantityField = new TextField("1");
        quantityField.setPromptText("Quantity");
        quantityField.setMaxWidth(140);

        Button buyButton = new Button("Buy selected");
        buyButton.setDefaultButton(true);
        buyButton.setOnAction(event -> buySelectedStock());

        Button sellButton = new Button("Sell selected share");
        sellButton.setOnAction(event -> sellSelectedShare());

        Button nextWeekButton = new Button("Next week");
        nextWeekButton.setOnAction(event -> advanceWeek());

        selectedStockLabel = new Label("Select a stock to buy.");
        selectedStockLabel.setTextFill(Color.web("#4f5865"));

        selectedShareLabel = new Label("Select a portfolio row to sell.");
        selectedShareLabel.setTextFill(Color.web("#4f5865"));

        HBox actions = new HBox(10, new Label("Quantity"), quantityField, buyButton, sellButton, nextWeekButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(10, new Separator(), selectedStockLabel, selectedShareLabel, actions);
        box.setPadding(new Insets(8, 0, 0, 0));
        return box;
    }

    private HBox createStatusLine() {
        messageLabel = new Label("Ready.");
        messageLabel.setTextFill(Color.web("#384150"));

        HBox statusLine = new HBox(messageLabel);
        statusLine.setPadding(new Insets(10, 18, 12, 18));
        statusLine.setAlignment(Pos.CENTER_LEFT);
        statusLine.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dde1e7; -fx-border-width: 1 0 0 0;");
        return statusLine;
    }

    private void buySelectedStock() {
        Stock stock = stockTable.getSelectionModel().getSelectedItem();
        if (stock == null) {
            showMessage("Select a stock before buying.", true);
            return;
        }

        BigDecimal quantity = parseQuantity();
        if (quantity == null) {
            return;
        }

        try {
            exchange.buy(stock.getSymbol(), quantity, player);
            showMessage("Bought " + formatQuantity(quantity) + " " + stock.getSymbol() + ".", false);
            refreshAll();
        } catch (Exception exception) {
            showMessage(exception.getMessage(), true);
        }
    }

    private void sellSelectedShare() {
        Share share = portfolioTable.getSelectionModel().getSelectedItem();
        if (share == null) {
            showMessage("Select a portfolio row before selling.", true);
            return;
        }

        try {
            exchange.sell(share, player);
            showMessage("Sold " + formatQuantity(share.getQuantity()) + " "
                    + share.getStock().getSymbol() + ".", false);
            refreshAll();
        } catch (Exception exception) {
            showMessage(exception.getMessage(), true);
        }
    }

    private void advanceWeek() {
        exchange.advance();
        showMessage("Advanced to week " + exchange.getWeek() + ".", false);
        refreshAll();
    }

    private BigDecimal parseQuantity() {
        String rawQuantity = quantityField.getText();
        if (rawQuantity == null || rawQuantity.isBlank()) {
            showMessage("Quantity must be filled in.", true);
            return null;
        }

        try {
            BigDecimal quantity = new BigDecimal(rawQuantity.trim());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                showMessage("Quantity must be greater than zero.", true);
                return null;
            }
            return quantity;
        } catch (NumberFormatException exception) {
            showMessage("Quantity must be a valid number.", true);
            return null;
        }
    }

    private void refreshAll() {
        stockRows.setAll(exchange.getAllStocks().stream()
                .sorted(Comparator.comparing(Stock::getSymbol))
                .toList());
        shareRows.setAll(player.getPortfolio().getShares());

        List<Transaction> transactions = new ArrayList<>(player.getTransactionArchive().getTransactions());
        transactions.sort(Comparator.comparingInt(Transaction::getWeek).reversed());
        transactionRows.setAll(transactions);

        weekLabel.setText(Integer.toString(exchange.getWeek()));
        cashLabel.setText(formatMoney(player.getMoney()));
        portfolioValueLabel.setText(formatMoney(player.getPortfolio().getNetWorth()));
        netWorthLabel.setText(formatMoney(player.getNetWorth()));
        statusLabel.setText(player.getStatus().name());

        updateSelectedStock(stockTable.getSelectionModel().getSelectedItem());
        updateSelectedShare(portfolioTable.getSelectionModel().getSelectedItem());
    }

    private void updateSelectedStock(Stock stock) {
        if (selectedStockLabel == null) {
            return;
        }
        if (stock == null) {
            selectedStockLabel.setText("Select a stock to buy.");
            return;
        }
        selectedStockLabel.setText(stock.getSymbol() + " " + stock.getCompany()
                + " | price " + formatMoney(stock.getSalesPrice())
                + " | latest change " + formatSignedMoney(stock.getLatestPriceChange()));
    }

    private void updateSelectedShare(Share share) {
        if (selectedShareLabel == null) {
            return;
        }
        if (share == null) {
            selectedShareLabel.setText("Select a portfolio row to sell.");
            return;
        }
        selectedShareLabel.setText("Holding " + formatQuantity(share.getQuantity()) + " "
                + share.getStock().getSymbol() + " | estimated sale proceeds "
                + formatMoney(new SaleCalculator(share).calculateTotal()));
    }

    private void showMessage(String message, boolean error) {
        messageLabel.setText(message == null || message.isBlank() ? "No details available." : message);
        messageLabel.setTextFill(error ? Color.web("#b42318") : Color.web("#1f6f43"));
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: 700;");
        return label;
    }

    private static Label metricLabel() {
        Label label = new Label();
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: 700;");
        return label;
    }

    private static <T> TableColumn<T, String> column(String title, TextProvider<T> textProvider, double width) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(textProvider.get(cell.getValue())));
        column.setMaxWidth(1f * Integer.MAX_VALUE * width);
        return column;
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

    @FunctionalInterface
    private interface TextProvider<T> {
        String get(T value);
    }
}
