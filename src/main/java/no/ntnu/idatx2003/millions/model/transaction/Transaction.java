package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.exception.InsufficientFundsException;
import no.ntnu.idatx2003.millions.exception.ShareNotOwnedException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.util.Validate;

/**
 * Abstract base class for transactions (purchases and sales).
 *
 * <p>{@code Transaction} is the abstract Command in the Command pattern: each
 * concrete subclass ({@link Purchase}, {@link Sale}) encapsulates a side
 * effect that can be applied to a {@link Player} at most once via
 * {@link #execute(Player)}.</p>
 */
public abstract class Transaction implements Command {
    private final Share share;
    private final int week;
    private final TransactionCalculator calculator;

    /**
     * Whether the transaction has been executed against a player account.
     */
    protected boolean committed;

    /**
     * Constructs a Transaction.
     *
     * @param share the {@link Share} involved in the transaction; must not be {@code null}
     * @param week the week in which the transaction occurs; must be non-negative
     * @param calculator the calculator for this transaction type; must not be {@code null}
     */
    public Transaction(Share share, int week, TransactionCalculator calculator) {
        this.share = Validate.requireNonNull(share, "share");
        this.week = Validate.requireInRange(week, 0, Integer.MAX_VALUE, "week");
        this.calculator = Validate.requireNonNull(calculator, "calculator");
        this.committed = false;
    }

    /**
     * Gets the share involved in the transaction.
     *
     * @return the Share, never {@code null}
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
     * @return the TransactionCalculator, never {@code null}
     */
    public TransactionCalculator getCalculator() {
        return calculator;
    }

    /**
     * Returns whether the transaction has already been executed.
     *
     * @return {@code true} if executed, {@code false} otherwise
     */
    @Override
    public boolean isExecuted() {
        return committed;
    }

    /**
     * Convenience alias for {@link #isExecuted()} kept for readability in
     * domain code that talks in terms of commits.
     *
     * @return {@code true} if the transaction has been committed
     */
    public boolean isCommitted() {
        return committed;
    }

    /**
     * Executes the transaction on the given player.
     * Subclasses define the concrete side effects.
     *
     * @param player the Player executing the transaction; must not be {@code null}
     * @throws TransactionAlreadyCommittedException if this transaction was already executed
     * @throws InsufficientFundsException if the player cannot afford a purchase
     * @throws ShareNotOwnedException if the player does not own the share being sold
     */
    @Override
    public abstract void execute(Player player)
            throws TransactionAlreadyCommittedException,
            InsufficientFundsException,
            ShareNotOwnedException;
}
