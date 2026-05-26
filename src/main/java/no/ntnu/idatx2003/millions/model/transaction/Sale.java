package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.exception.ShareNotOwnedException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;

/**
 * Concrete Command for selling shares.
 *
 * <p>When executed, removes the matching share (or part of it) from the
 * player's portfolio and credits the sale proceeds to the player.</p>
 */
public class Sale extends Transaction {
    private final Share portfolioShare;

    /**
     * Constructs a Sale transaction selling the entire share.
     *
     * @param share the Share being sold; must not be {@code null}
     * @param week the week in which the sale occurs
     * @param calculator the SaleCalculator; must not be {@code null}
     */
    public Sale(Share share, int week, TransactionCalculator calculator) {
        this(share, share, week, calculator);
    }

    /**
     * Constructs a Sale for a portion of an existing portfolio share.
     *
     * @param share the Share quantity being sold; must not be {@code null}
     * @param portfolioShare the Share object currently held in the player's portfolio; must not be {@code null}
     * @param week the week in which the sale occurs
     * @param calculator the SaleCalculator; must not be {@code null}
     * @throws IllegalArgumentException if {@code share}'s quantity exceeds {@code portfolioShare}'s quantity
     */
    public Sale(Share share, Share portfolioShare, int week, TransactionCalculator calculator) {
        super(share, week, calculator);
        Validate.requireNonNull(portfolioShare, "portfolioShare");
        if (share.getQuantity().compareTo(portfolioShare.getQuantity()) > 0) {
            throw new IllegalArgumentException("Cannot sell more shares than the portfolio contains");
        }
        this.portfolioShare = portfolioShare;
    }

    /**
     * Executes the sale against the player's account.
     *
     * <p>Preconditions:</p>
     * <ul>
     *     <li>The transaction must not already be executed.</li>
     *     <li>The player must own the portfolio share being sold.</li>
     * </ul>
     *
     * @param player the player executing the sale; must not be {@code null}
     * @throws TransactionAlreadyCommittedException if the sale was already executed
     * @throws ShareNotOwnedException if the share is not in the player's portfolio
     */
    @Override
    public void execute(Player player)
            throws TransactionAlreadyCommittedException, ShareNotOwnedException {
        Validate.requireNonNull(player, "player");
        if (committed) {
            throw new TransactionAlreadyCommittedException(
                    "Transaction has already been committed");
        }
        if (!player.getPortfolio().contains(portfolioShare)) {
            throw new ShareNotOwnedException(
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

        BigDecimal total = getCalculator().calculateTotal();
        player.addMoney(total);
        player.getTransactionArchive().add(this);
        committed = true;
    }
}
