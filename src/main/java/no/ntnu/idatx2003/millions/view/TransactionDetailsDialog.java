package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatx2003.millions.model.transaction.Purchase;
import no.ntnu.idatx2003.millions.model.transaction.Sale;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.model.transaction.TransactionCalculator;
import no.ntnu.idatx2003.millions.util.Validate;

/**
 * Read-only details dialog for an archived transaction.
 */
public final class TransactionDetailsDialog extends Alert {

    /**
     * Creates a transaction details dialog.
     *
     * @param transaction the transaction to display; must not be {@code null}
     */
    public TransactionDetailsDialog(Transaction transaction) {
        super(AlertType.INFORMATION);
        Validate.requireNonNull(transaction, "transaction");

        setTitle("Transaction details");
        setHeaderText(typeText(transaction) + " - week " + transaction.getWeek());
        getButtonTypes().setAll(ButtonType.OK);
        getDialogPane().setContent(buildContent(transaction));
    }

    private GridPane buildContent(Transaction transaction) {
        TransactionCalculator calculator = transaction.getCalculator();
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 4, 4, 4));

        int row = 0;
        addRow(grid, row++, "Week", Integer.toString(transaction.getWeek()));
        addRow(grid, row++, "Type", typeText(transaction));
        addRow(grid, row++, "Symbol", transaction.getShare().getStock().getSymbol());
        addRow(grid, row++, "Company", transaction.getShare().getStock().getCompany());
        addRow(grid, row++, "Quantity", ViewFormat.quantity(transaction.getShare().getQuantity()));
        addRow(grid, row++, "Price", ViewFormat.money(transaction.getShare().getStock().getSalesPrice()));
        addRow(grid, row++, "Gross", ViewFormat.money(calculator.calculateGross()));
        addRow(grid, row++, "Commission", ViewFormat.money(calculator.calculateCommission()));
        addRow(grid, row++, "Tax", ViewFormat.money(calculator.calculateTax()));
        addRow(grid, row, transaction instanceof Sale ? "Total proceeds" : "Total cost",
                ViewFormat.money(calculator.calculateTotal()));
        return grid;
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

    private static void addRow(GridPane grid, int row, String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metric-label");
        Label valueLabel = new Label(value);
        grid.add(keyLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }
}
