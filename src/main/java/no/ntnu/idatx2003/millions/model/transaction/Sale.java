package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.exception.ShareNotInPortfolioException;

import java.math.BigDecimal;

/**
 * Represents a sale transaction.
 * When committed, adds money to the player and removes shares from their portfolio.
 */
public class Sale extends Transaction {
    private final Share portfolioShare;

    /**
     * Constructs a Sale transaction.
     *
     * @param share the Share being sold
     * @param week the week in which the sale occurs
     * @param calculator the SaleCalculator
     */
    public Sale(Share share, int week, TransactionCalculator calculator) {
        this(share, share, week, calculator);
    }

    /**
     * Constructs a Sale transaction for part of an existing portfolio share.
     *
     * @param share the Share quantity being sold
     * @param portfolioShare the Share object currently held in the player's portfolio
     * @param week the week in which the sale occurs
     * @param calculator the SaleCalculator
     */
    public Sale(Share share, Share portfolioShare, int week, TransactionCalculator calculator) {
        super(share, week, calculator);
        if (portfolioShare == null) {
            throw new IllegalArgumentException("Portfolio share cannot be null");
        }
        if (share.getQuantity().compareTo(portfolioShare.getQuantity()) > 0) {
            throw new IllegalArgumentException("Cannot sell more shares than the portfolio contains");
        }
        this.portfolioShare = portfolioShare;
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

        if (!player.getPortfolio().contains(portfolioShare)) {
            throw new ShareNotInPortfolioException(
                    "Share not found in portfolio: " + portfolioShare);
        }

        player.getPortfolio().removeShare(portfolioShare);
        BigDecimal remainingQuantity = portfolioShare.getQuantity().subtract(getShare().getQuantity());
        if (remainingQuantity.compareTo(BigDecimal.ZERO) > 0) {
            player.getPortfolio().addShare(new Share(
                    portfolioShare.getStock(),
                    remainingQuantity,
                    portfolioShare.getPurchasePrice()));
        }

        java.math.BigDecimal total = getCalculator().calculateTotal();
        player.addMoney(total);
        player.getTransactionArchive().add(this);

        committed = true;
    }
}
