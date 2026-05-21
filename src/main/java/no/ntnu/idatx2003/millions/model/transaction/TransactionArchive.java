package no.ntnu.idatx2003.millions.model.transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Archive for storing and retrieving transactions.
 * Provides methods to query transactions by week and type.
 */
public class TransactionArchive {
    private final List<Transaction> transactions;

    /**
     * Constructs an empty TransactionArchive.
     */
    public TransactionArchive() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Adds a transaction to the archive.
     *
     * @param transaction the Transaction to add
     * @return true if successfully added, false if null
     */
    public boolean add(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        return transactions.add(transaction);
    }

    /**
     * Checks if the archive is empty.
     *
     * @return true if no transactions have been added
     */
    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    /**
     * Gets all transactions in the archive.
     *
     * @return an unmodifiable list of all transactions
     */
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    /**
     * Gets all transactions for a specific week.
     *
     * @param week the week number
     * @return an unmodifiable list of transactions in that week
     */
    public List<Transaction> getTransactions(int week) {
        return transactions.stream()
                .filter(t -> t.getWeek() == week)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all purchase transactions for a specific week.
     *
     * @param week the week number
     * @return an unmodifiable list of purchases in that week
     */
    public List<Transaction> getPurchases(int week) {
        return transactions.stream()
                .filter(t -> t instanceof Purchase && t.getWeek() == week)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all sale transactions for a specific week.
     *
     * @param week the week number
     * @return an unmodifiable list of sales in that week
     */
    public List<Transaction> getSales(int week) {
        return transactions.stream()
                .filter(t -> t instanceof Sale && t.getWeek() == week)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Counts the number of distinct weeks with transactions.
     * Used to determine player status.
     *
     * @return the count of weeks with at least one transaction
     */
    public int countDistinctWeeks() {
        return (int) transactions.stream()
                .map(Transaction::getWeek)
                .collect(Collectors.toUnmodifiableSet())
                .size();
    }
}

