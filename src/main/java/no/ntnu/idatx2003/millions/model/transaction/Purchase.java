package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.exception.InsufficientFundsException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;

/**
 * Represents a purchase transaction.
 * When committed, deducts money from the player and adds shares to their portfolio.
 */
public class Purchase extends Transaction {
    /**
     * Constructs a Purchase transaction.
     *
     * @param share the Share being purchased
     * @param week the week in which the purchase occurs
     * @param calculator the PurchaseCalculator
     */
    public Purchase(Share share, int week, TransactionCalculator calculator) {
        super(share, week, calculator);
    }

    /**
     * Commits the purchase transaction.
     * Preconditions:
     * - Transaction must not already be committed
     * - Player must have sufficient funds
     *
     * @param player the Player executing the purchase
     * @throws TransactionAlreadyCommittedException if already committed
     * @throws InsufficientFundsException if player doesn't have enough money
     */
    @Override
    public void commit(Player player) throws TransactionAlreadyCommittedException, InsufficientFundsException {
        if (committed) {
            throw new TransactionAlreadyCommittedException(
                    "Transaction has already been committed");
        }

        java.math.BigDecimal total = getCalculator().calculateTotal();

        if (player.getMoney().compareTo(total) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds for purchase. Required: " + total +
                            ", Available: " + player.getMoney());
        }

        player.withdrawMoney(total);
        player.getPortfolio().addShare(getShare());
        player.getTransactionArchive().add(this);

        committed = true;
    }
}

