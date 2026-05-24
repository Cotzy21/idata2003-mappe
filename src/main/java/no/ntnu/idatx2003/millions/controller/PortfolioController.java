package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.transaction.SaleCalculator;
import no.ntnu.idatx2003.millions.view.MainView;

import java.math.BigDecimal;
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

    private void sell(Share share, BigDecimal quantity) {
        try {
            session.exchange().sell(share, quantity, session.player());
            view.showMessage("Sold " + ControllerFormat.quantity(quantity) + " "
                    + share.getStock().getSymbol() + ".", false);
            refreshAll.run();
        } catch (Exception exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    private void showQuantityError(String message) {
        view.showMessage(message, true);
    }
}
