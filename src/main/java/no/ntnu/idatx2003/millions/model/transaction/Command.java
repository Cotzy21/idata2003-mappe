package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.exception.InsufficientFundsException;
import no.ntnu.idatx2003.millions.exception.ShareNotOwnedException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.model.Player;

/**
 * Command interface for transactions that can be executed against a {@link Player}.
 *
 * <p>This is an explicit application of the <em>Command</em> design pattern.
 * The receiver is a {@link Player}; concrete commands (currently
 * {@link Purchase} and {@link Sale}) encapsulate the side effects that must
 * happen on commit. The invoker is anything that holds a {@link Transaction}
 * reference – typically {@link no.ntnu.idatx2003.millions.model.Exchange}.</p>
 *
 * <p>Each command may be executed at most once. Re-execution raises
 * {@link TransactionAlreadyCommittedException}.</p>
 */
public interface Command {

    /**
     * Executes the command against the supplied player.
     *
     * @param player the player to apply the command to; must not be {@code null}
     * @throws TransactionAlreadyCommittedException if the command has already been executed
     * @throws InsufficientFundsException if the player cannot afford the purchase
     * @throws ShareNotOwnedException if the player does not own the share being sold
     */
    void execute(Player player)
            throws TransactionAlreadyCommittedException,
            InsufficientFundsException,
            ShareNotOwnedException;

    /**
     * Returns whether the command has already been executed successfully.
     *
     * @return {@code true} if executed, {@code false} otherwise
     */
    boolean isExecuted();
}
