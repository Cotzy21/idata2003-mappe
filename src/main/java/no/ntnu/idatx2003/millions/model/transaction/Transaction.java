package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Share;

/**
 * Abstract base class for transactions (purchases and sales).
 * Tracks the share, week, calculator, and commit status.
 */
public abstract class Transaction {
    private final Share share;
    private final int week;
    private final TransactionCalculator calculator;

    /**
     * Whether the transaction has been committed to a player account.
     */
    protected boolean committed;

    /**
     * Constructs a Transaction.
     *
     * @param share the Share involved in the transaction
     * @param week the week in which the transaction occurs
     * @param calculator the calculator for this transaction type
     */
    public Transaction(Share share, int week, TransactionCalculator calculator) {
        this.share = share;
        this.week = week;
        this.calculator = calculator;
        this.committed = false;
    }

    /**
     * Gets the share involved in the transaction.
     *
     * @return the Share
     */
    public Share getShare() {
        return share;
    }

    /**
     * Gets the week in which the transaction occurs.
     *
     * @return the week number
     */
    public int getWeek() {
        return week;
    }

    /**
     * Gets the calculator for this transaction.
     *
     * @return the TransactionCalculator
     */
    public TransactionCalculator getCalculator() {
        return calculator;
    }

    /**
     * Checks if the transaction has been committed.
     *
     * @return true if committed, false otherwise
     */
    public boolean isCommitted() {
        return committed;
    }

    /**
     * Commits the transaction.
     * This is implemented by subclasses (Purchase or Sale).
     *
     * @param player the Player executing the transaction
     * @throws Exception if preconditions are not met or transaction is already committed
     */
    public abstract void commit(no.ntnu.idatx2003.millions.model.Player player) throws Exception;
}
