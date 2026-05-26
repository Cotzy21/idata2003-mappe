package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.TransactionCalculator;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;

/**
 * Modal preview dialog shown before a transaction is committed, so the
 * player can confirm fees, taxes and totals.
 *
 * <p>The dialog is intentionally decoupled from the concrete
 * {@link no.ntnu.idatx2003.millions.model.transaction.Transaction} class. It
 * only depends on the calculator strategy, which keeps it usable both for
 * purchases and sales.</p>
 */
public final class TransactionPreviewDialog extends Dialog<Boolean> {

    /**
     * Type of transaction shown in the preview header.
     */
    public enum Kind {
        /** Purchase preview. */
        BUY("Confirm purchase"),
        /** Sale preview. */
        SELL("Confirm sale");

        private final String headerText;

        Kind(String headerText) {
            this.headerText = headerText;
        }

        String headerText() {
            return headerText;
        }
    }

    /**
     * Creates a new preview dialog.
     *
     * @param kind the type of transaction being previewed; must not be {@code null}
     * @param stock the stock involved; must not be {@code null}
     * @param quantity the quantity being traded; must be positive
     * @param pricePerShare the per-share price used by the calculator; must be non-negative
     * @param calculator the transaction calculator strategy; must not be {@code null}
     */
    public TransactionPreviewDialog(Kind kind, Stock stock, BigDecimal quantity,
                                    BigDecimal pricePerShare, TransactionCalculator calculator) {
        Validate.requireNonNull(kind, "kind");
        Validate.requireNonNull(stock, "stock");
        Validate.requirePositive(quantity, "quantity");
        Validate.requireNonNegative(pricePerShare, "pricePerShare");
        Validate.requireNonNull(calculator, "calculator");

        setTitle(kind.headerText());
        setHeaderText(kind == Kind.BUY
                ? "Review purchase before commit"
                : "Review sale before commit");

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 4, 4, 4));

        int row = 0;
        addRow(grid, row++, "Symbol", stock.getSymbol());
        addRow(grid, row++, "Company", stock.getCompany());
        addRow(grid, row++, "Quantity", ViewFormat.quantity(quantity));
        addRow(grid, row++, "Price per share", ViewFormat.money(pricePerShare));
        addRow(grid, row++, "Gross", ViewFormat.money(calculator.calculateGross()));
        addRow(grid, row++, "Commission", ViewFormat.money(calculator.calculateCommission()));
        addRow(grid, row++, "Tax", ViewFormat.money(calculator.calculateTax()));

        Label totalKey = new Label(kind == Kind.BUY ? "Total cost" : "Total proceeds");
        totalKey.getStyleClass().add("metric-label");
        Label totalValue = new Label(ViewFormat.money(calculator.calculateTotal()));
        totalValue.getStyleClass().add("metric-value");
        grid.add(totalKey, 0, row);
        grid.add(totalValue, 1, row);

        getDialogPane().setContent(grid);
        ButtonType confirm = new ButtonType(kind == Kind.BUY ? "Buy" : "Sell",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().setAll(confirm, ButtonType.CANCEL);
        setResultConverter(button -> button == confirm);
    }

    private static void addRow(GridPane grid, int row, String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metric-label");
        Label valueLabel = new Label(value);
        grid.add(keyLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }
}
