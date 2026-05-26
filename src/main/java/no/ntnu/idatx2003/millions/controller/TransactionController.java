package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.StockSearch;
import no.ntnu.idatx2003.millions.model.transaction.Purchase;
import no.ntnu.idatx2003.millions.model.transaction.Sale;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Handles transaction list presentation.
 */
public class TransactionController {
    private List<Transaction> currentTransactions = List.of();

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
        currentTransactions = player.getTransactionArchive().getTransactions().stream()
                .sorted(Comparator.comparingInt(Transaction::getWeek).reversed())
                .toList();
        return currentTransactions;
    }

    /**
     * Applies text, type and week filters to the most recently loaded
     * transaction list.
     *
     * @param text free-text search for symbol or company, may be blank
     * @param type one of {@code Alle}, {@code Kjøp} or {@code Salg}
     * @param week week to filter by, where 0 means all weeks
     * @return filtered transactions
     */
    public List<Transaction> applyFilter(String text, String type, int week) {
        return currentTransactions.stream()
                .filter(transaction -> StockSearch.matches(transaction.getShare().getStock(), text))
                .filter(transaction -> matchesType(transaction, type))
                .filter(transaction -> week == 0 || transaction.getWeek() == week)
                .toList();
    }

    private static boolean matchesType(Transaction transaction, String type) {
        return type == null || "Alle".equals(type)
                || ("Kjøp".equals(type) && transaction instanceof Purchase)
                || ("Salg".equals(type) && transaction instanceof Sale);
    }
}
