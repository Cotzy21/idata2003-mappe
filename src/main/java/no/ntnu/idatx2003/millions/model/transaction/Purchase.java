package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.exception.InsufficientFundsException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;

/**
 * Concrete Command for purchasing shares.
 *
 * <p>When executed, deducts the calculated total from the player and adds the
 * share to their portfolio.</p>
 */
public class Purchase extends Transaction {

    /**
     * Constructs a Purchase transaction.
     *
     * @param share the Share being purchased; must not be {@code null}
     * @param week the week in which the purchase occurs
     * @param calculator the PurchaseCalculator; must not be {@code null}
     */
    public Purchase(Share share, int week, TransactionCalculator calculator) {
        super(share, week, calculator);
    }

    /**
     * Executes the purchase against the player's account.
     *
     * <p>Preconditions:</p>
     * <ul>
     *     <li>The transaction must not already be executed.</li>
     *     <li>The player must have at least {@code calculator.calculateTotal()} cash.</li>
     * </ul>
     *
     * @param player the player executing the purchase; must not be {@code null}
     * @throws TransactionAlreadyCommittedException if the purchase was already executed
     * @throws InsufficientFundsException if the player does not have enough money
     */
    @Override
    public void execute(Player player)
            throws TransactionAlreadyCommittedException, InsufficientFundsException {
        Validate.requireNonNull(player, "player");
        if (committed) {
            throw new TransactionAlreadyCommittedException(
                    "Transaction has already been committed");
        }

        BigDecimal total = getCalculator().calculateTotal();
        if (player.getMoney().compareTo(total) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds for purchase. Required: " + total
                            + ", Available: " + player.getMoney());
        }

        player.withdrawMoney(total);
        player.getPortfolio().addShare(getShare());
        player.getTransactionArchive().add(this);
        committed = true;
    }
}
