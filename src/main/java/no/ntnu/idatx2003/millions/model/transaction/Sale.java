package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.exception.ShareNotInPortfolioException;

/**
 * Represents a sale transaction.
 * When committed, adds money to the player and removes shares from their portfolio.
 */
public class Sale extends Transaction {
    /**
     * Constructs a Sale transaction.
     *
     * @param share the Share being sold
     * @param week the week in which the sale occurs
     * @param calculator the SaleCalculator
     */
    public Sale(Share share, int week, TransactionCalculator calculator) {
        super(share, week, calculator);
    }

    /**
     * Commits the sale transaction.
     * Preconditions:
     * - Transaction must not already be committed
     * - Player's portfolio must contain the share
     *
     * @param player the Player executing the sale
     * @throws TransactionAlreadyCommittedException if already committed
     * @throws ShareNotInPortfolioException if the share is not in the player's portfolio
     */
    @Override
    public void commit(Player player) throws TransactionAlreadyCommittedException, ShareNotInPortfolioException {
        if (committed) {
            throw new TransactionAlreadyCommittedException(
                    "Transaction has already been committed");
        }

        if (!player.getPortfolio().contains(getShare())) {
            throw new ShareNotInPortfolioException(
                    "Share not found in portfolio: " + getShare());
        }

        player.getPortfolio().removeShare(getShare());
        java.math.BigDecimal total = getCalculator().calculateTotal();
        player.addMoney(total);
        player.getTransactionArchive().add(this);

        committed = true;
    }
}

