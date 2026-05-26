package no.ntnu.idatx2003.millions.controller;

import javafx.application.Platform;
import no.ntnu.idatx2003.millions.exception.ShareNotOwnedException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.util.Validate;
import no.ntnu.idatx2003.millions.model.transaction.SaleCalculator;
import no.ntnu.idatx2003.millions.view.MainView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles portfolio actions and selected-share presentation.
 */
public class PortfolioController {
    private final MainView view;
    private final GameSession session;
    private final Runnable refreshAll;

    /**
     * Creates a portfolio controller.
     *
     * @param view the view to update; must not be {@code null}
     * @param session the game session; must not be {@code null}
     * @param refreshAll callback for refreshing the full view; must not be {@code null}
     */
    public PortfolioController(MainView view, GameSession session, Runnable refreshAll) {
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.session = Objects.requireNonNull(session, "session must not be null");
        this.refreshAll = Objects.requireNonNull(refreshAll, "refreshAll must not be null");
    }

    /**
     * Sells the selected share using the quantity from the view.
     */
    public void sellSelectedShare() {
        Share share = view.getSelectedShare();
        if (share == null) {
            view.showMessage("Select a portfolio row before selling.", true);
            return;
        }

        QuantityParser.parse(view.getQuantityText(), this::showQuantityError)
                .ifPresent(quantity -> sell(share, quantity));
    }

    /**
     * Updates selected share details.
     *
     * @param share the selected share, or {@code null}
     */
    public void showShareDetails(Share share) {
        if (share == null) {
            view.clearSelectedShareDetails();
            return;
        }
        view.setSelectedShareDetails("Holding " + ControllerFormat.quantity(share.getQuantity()) + " "
                + share.getStock().getSymbol() + " | estimated sale proceeds "
                + ControllerFormat.money(new SaleCalculator(share).calculateTotal()));
    }

    /**
     * Confirms liquidation, sells every portfolio row and exits the application
     * after showing a final receipt.
     */
    public void liquidateAndExit() {
        BigDecimal estimatedFinalWorth = session.player().getMoney()
                .add(session.player().getPortfolio().getNetWorth());
        if (!view.confirmLiquidation(estimatedFinalWorth)) {
            view.showMessage("Avslutning avbrutt.", false);
            return;
        }

        try {
            List<Transaction> transactions = liquidate();
            refreshAll.run();
            view.showFinalReceipt(session.player(), session.exchange().getWeek(), transactions);
            Platform.exit();
        } catch (ShareNotOwnedException | TransactionAlreadyCommittedException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Sells all currently held portfolio rows and returns the sale transactions.
     *
     * @return transactions created by the liquidation
     * @throws ShareNotOwnedException if a portfolio row cannot be sold
     * @throws TransactionAlreadyCommittedException if a generated sale is already committed
     */
    public List<Transaction> liquidate()
            throws ShareNotOwnedException, TransactionAlreadyCommittedException {
        return liquidate(session.exchange(), session.player());
    }

    /**
     * Sells all portfolio rows for a player through an exchange.
     *
     * @param exchange the exchange handling the sales; must not be {@code null}
     * @param player the player to liquidate; must not be {@code null}
     * @return transactions created by the liquidation
     * @throws ShareNotOwnedException if a portfolio row cannot be sold
     * @throws TransactionAlreadyCommittedException if a generated sale is already committed
     */
    public static List<Transaction> liquidate(Exchange exchange, Player player)
            throws ShareNotOwnedException, TransactionAlreadyCommittedException {
        Validate.requireNonNull(exchange, "exchange");
        Validate.requireNonNull(player, "player");
        List<Share> sharesToSell = player.getPortfolio().getShares();
        List<Transaction> transactions = new ArrayList<>();
        for (Share share : sharesToSell) {
            transactions.add(exchange.sell(share, player));
        }
        return List.copyOf(transactions);
    }

    private void sell(Share share, BigDecimal quantity) {
        try {
            session.exchange().sell(share, quantity, session.player());
            view.showMessage("Sold " + ControllerFormat.quantity(quantity) + " "
                    + share.getStock().getSymbol() + ".", false);
            refreshAll.run();
        } catch (ShareNotOwnedException | TransactionAlreadyCommittedException
                | IllegalArgumentException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    private void showQuantityError(String message) {
        view.showMessage(message, true);
    }
}
