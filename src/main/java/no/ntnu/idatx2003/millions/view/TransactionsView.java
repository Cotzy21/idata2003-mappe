package no.ntnu.idatx2003.millions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2003.millions.model.StockSearch;
import no.ntnu.idatx2003.millions.model.transaction.Purchase;
import no.ntnu.idatx2003.millions.model.transaction.Sale;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.util.List;

/**
 * Displays archived transactions with text, type and week filters.
 */
public class TransactionsView {
    private final ObservableList<Transaction> transactionRows = FXCollections.observableArrayList();
    private final FilteredList<Transaction> filteredTransactionRows = new FilteredList<>(transactionRows);
    private final VBox root;
    private final TextField searchField;
    private final ChoiceBox<String> typeFilter;
    private final Spinner<Integer> weekFilter;
    private final TableView<Transaction> transactionTable;

    /**
     * Creates the transactions view.
     */
    public TransactionsView() {
        searchField = new TextField();
        typeFilter = new ChoiceBox<>(FXCollections.observableArrayList("Alle", "Kjøp", "Salg"));
        weekFilter = new Spinner<>(0, 10_000, 0);
        transactionTable = createTransactionTable();

        configureFilters();

        root = new VBox(10, sectionTitle("Transactions"), createFilterPane(), transactionTable);
        root.getStyleClass().add("section-pane");
        root.setPadding(new Insets(18));
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
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
     * Replaces the transaction rows shown by the table.
     *
     * @param transactions the transactions to show
     */
    public void setTransactions(List<Transaction> transactions) {
        transactionRows.setAll(transactions);
    }

    /**
     * Clears the transaction table selection.
     */
    public void clearSelection() {
        transactionTable.getSelectionModel().clearSelection();
    }

    private GridPane createFilterPane() {
        GridPane filters = new GridPane();
        filters.setHgap(10);
        filters.setVgap(8);

        searchField.setPromptText("Søk symbol eller selskap");
        typeFilter.setValue("Alle");
        weekFilter.setEditable(true);
        weekFilter.setPrefWidth(110);

        filters.add(new Label("Søk"), 0, 0);
        filters.add(searchField, 1, 0);
        filters.add(new Label("Type"), 2, 0);
        filters.add(typeFilter, 3, 0);
        filters.add(new Label("Uke"), 4, 0);
        filters.add(weekFilter, 5, 0);
        return filters;
    }

    private void configureFilters() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        weekFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        applyFilter();
    }

    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>(filteredTransactionRows);
        table.getStyleClass().add("transaction-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No transactions yet"));

        TableColumn<Transaction, String> week = TableColumns.column("Uke",
                transaction -> Integer.toString(transaction.getWeek()), 0.08);
        TableColumn<Transaction, String> type = TableColumns.column("Type", TransactionsView::typeText, 0.10);
        TableColumn<Transaction, String> symbol = TableColumns.column("Symbol",
                transaction -> transaction.getShare().getStock().getSymbol(), 0.12);
        TableColumn<Transaction, String> quantity = TableColumns.column("Kvantitet",
                transaction -> ViewFormat.quantity(transaction.getShare().getQuantity()), 0.12);
        TableColumn<Transaction, String> price = TableColumns.column("Pris",
                transaction -> ViewFormat.money(transaction.getShare().getStock().getSalesPrice()), 0.12);
        TableColumn<Transaction, String> gross = TableColumns.column("Brutto",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateGross()), 0.12);
        TableColumn<Transaction, String> commission = TableColumns.column("Kurtasje",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateCommission()), 0.12);
        TableColumn<Transaction, String> tax = TableColumns.column("Skatt",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateTax()), 0.10);
        TableColumn<Transaction, String> total = TableColumns.column("Total",
                transaction -> ViewFormat.money(transaction.getCalculator().calculateTotal()), 0.12);

        table.getColumns().setAll(List.of(week, type, symbol, quantity, price, gross, commission, tax, total));
        table.setRowFactory(unused -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    new TransactionDetailsDialog(row.getItem()).showAndWait();
                }
            });
            return row;
        });
        return table;
    }

    private void applyFilter() {
        String text = searchField.getText();
        String type = typeFilter.getValue();
        int week = weekFilter.getValue() == null ? 0 : weekFilter.getValue();
        filteredTransactionRows.setPredicate(transaction ->
                matchesText(transaction, text) && matchesType(transaction, type) && matchesWeek(transaction, week));
    }

    private static boolean matchesText(Transaction transaction, String text) {
        return StockSearch.matches(transaction.getShare().getStock(), text);
    }

    private static boolean matchesType(Transaction transaction, String type) {
        return type == null || "Alle".equals(type)
                || ("Kjøp".equals(type) && transaction instanceof Purchase)
                || ("Salg".equals(type) && transaction instanceof Sale);
    }

    private static boolean matchesWeek(Transaction transaction, int week) {
        return week == 0 || transaction.getWeek() == week;
    }

    private static String typeText(Transaction transaction) {
        if (transaction instanceof Purchase) {
            return "Kjøp";
        }
        if (transaction instanceof Sale) {
            return "Salg";
        }
        return transaction.getClass().getSimpleName();
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }
}
