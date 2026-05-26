package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Final game receipt shown after all shares have been liquidated.
 */
public final class FinalReceiptDialog extends Alert {

    /**
     * Creates a final receipt dialog.
     *
     * @param player the player after liquidation; must not be {@code null}
     * @param week the final week number
     * @param transactions transactions created by liquidation; must not be {@code null}
     */
    public FinalReceiptDialog(Player player, int week, List<Transaction> transactions) {
        super(AlertType.INFORMATION);
        Validate.requireNonNull(player, "player");
        Validate.requireNonNull(transactions, "transactions");

        setTitle("Game finished");
        setHeaderText("Sluttoppgjør");
        getButtonTypes().setAll(ButtonType.OK);
        getDialogPane().setContent(buildContent(player, week, transactions));
    }

    private GridPane buildContent(Player player, int week, List<Transaction> transactions) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 4, 4, 4));

        int row = 0;
        addRow(grid, row++, "Spiller", player.getName());
        addRow(grid, row++, "Startkapital", ViewFormat.money(player.getStartingMoney()));
        addRow(grid, row++, "Sluttbeholdning", ViewFormat.money(player.getNetWorth()));
        addRow(grid, row++, "Avkastning", returnPercent(player));
        addRow(grid, row++, "Uker spilt", Integer.toString(week));
        addRow(grid, row++, "Salg ved avslutning", Integer.toString(transactions.size()));
        addRow(grid, row++, "Transaksjoner totalt",
                Integer.toString(player.getTransactionArchive().getTransactions().size()));
        addRow(grid, row, "Status", player.getStatus().name());
        return grid;
    }

    private static String returnPercent(Player player) {
        if (player.getStartingMoney().compareTo(BigDecimal.ZERO) == 0) {
            return "0.00%";
        }
        BigDecimal returnPercent = player.getNetWorth()
                .subtract(player.getStartingMoney())
                .multiply(new BigDecimal("100"))
                .divide(player.getStartingMoney(), 2, RoundingMode.HALF_UP);
        return returnPercent.toPlainString() + "%";
    }

    private static void addRow(GridPane grid, int row, String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metric-label");
        Label valueLabel = new Label(value);
        grid.add(keyLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }
}
