package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Handles transaction list presentation.
 */
public class TransactionController {

    /**
     * Creates a transaction controller.
     */
    public TransactionController() {
        // Stateless controller.
    }

    /**
     * Returns transactions sorted by newest week first.
     *
     * @param player the player whose archive should be read; must not be {@code null}
     * @return sorted transactions, never {@code null}
     */
    public List<Transaction> getTransactionsNewestFirst(Player player) {
        Objects.requireNonNull(player, "player must not be null");
        return player.getTransactionArchive().getTransactions().stream()
                .sorted(Comparator.comparingInt(Transaction::getWeek).reversed())
                .toList();
    }
}
