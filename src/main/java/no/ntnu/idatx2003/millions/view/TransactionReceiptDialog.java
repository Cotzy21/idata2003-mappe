package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.transaction.Purchase;
import no.ntnu.idatx2003.millions.model.transaction.Sale;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.model.transaction.TransactionCalculator;
import no.ntnu.idatx2003.millions.util.Validate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Receipt dialog shown after a transaction has been committed successfully.
 *
 * <p>Displays the per-row breakdown of fees and totals together with the
 * player's updated cash, portfolio value and net worth.</p>
 */
public final class TransactionReceiptDialog extends Alert {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Creates a receipt for a freshly committed transaction.
     *
     * @param transaction the committed transaction; must not be {@code null}
     * @param player the player whose account was updated; must not be {@code null}
     */
    public TransactionReceiptDialog(Transaction transaction, Player player) {
        super(AlertType.INFORMATION);
        Validate.requireNonNull(transaction, "transaction");
        Validate.requireNonNull(player, "player");

        boolean isSale = transaction instanceof Sale;
        boolean isPurchase = transaction instanceof Purchase;
        String label = isSale ? "Sale receipt" : (isPurchase ? "Purchase receipt" : "Receipt");
        setTitle(label);
        setHeaderText(label);

        getButtonTypes().setAll(ButtonType.OK);
        getDialogPane().setContent(buildContent(transaction, player));
    }

    private GridPane buildContent(Transaction transaction, Player player) {
        TransactionCalculator calculator = transaction.getCalculator();
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 4, 4, 4));

        int row = 0;
        addRow(grid, row++, "Timestamp", LocalDateTime.now().format(TIMESTAMP_FORMAT));
        addRow(grid, row++, "Week", Integer.toString(transaction.getWeek()));
        addRow(grid, row++, "Symbol", transaction.getShare().getStock().getSymbol());
        addRow(grid, row++, "Company", transaction.getShare().getStock().getCompany());
        addRow(grid, row++, "Quantity", ViewFormat.quantity(transaction.getShare().getQuantity()));
        addRow(grid, row++, "Price per share",
                ViewFormat.money(transaction.getShare().getStock().getSalesPrice()));
        addRow(grid, row++, "Gross", ViewFormat.money(calculator.calculateGross()));
        addRow(grid, row++, "Commission", ViewFormat.money(calculator.calculateCommission()));
        addRow(grid, row++, "Tax", ViewFormat.money(calculator.calculateTax()));
        addRow(grid, row++, transaction instanceof Sale ? "Total proceeds" : "Total cost",
                ViewFormat.money(calculator.calculateTotal()));
        addRow(grid, row++, "Cash after", ViewFormat.money(player.getMoney()));
        addRow(grid, row++, "Portfolio value", ViewFormat.money(player.getPortfolio().getNetWorth()));
        addRow(grid, row++, "Net worth", ViewFormat.money(player.getNetWorth()));
        addRow(grid, row, "Status", player.getStatus().name());
        return grid;
    }

    private static void addRow(GridPane grid, int row, String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metric-label");
        Label valueLabel = new Label(value);
        grid.add(keyLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }
}
