package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.exception.ShareNotOwnedException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Sale Transaction Tests")
class SaleTest {
    private Sale sale;
    private Stock stock;
    private Share share;
    private Player player;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("140.00"));
        sale = TransactionFactory.createSale(share, 1);
        player = new Player("TestPlayer", new BigDecimal("5000.00"));
        // Add share to player's portfolio
        player.getPortfolio().addShare(share);
    }

    @DisplayName("execute: successfully sells shares when present in portfolio")
    @Test
    void execute_whenShareOwned_completesSale() throws Exception {
        BigDecimal initialMoney = player.getMoney();
        sale.execute(player);

        assertTrue(sale.isCommitted());
        assertTrue(sale.isExecuted());
        // Money should be increased (1500.00 - 15.00 - 25.50 = 1459.50)
        BigDecimal expectedMoney = initialMoney.add(new BigDecimal("1459.50"));
        assertEquals(0, player.getMoney().compareTo(expectedMoney));
        // Share should be removed from portfolio
        assertEquals(0, player.getPortfolio().getShares().size());
        // Transaction should be in archive
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("execute: sells partial quantity and keeps remaining shares")
    @Test
    void execute_whenPartialQuantity_keepsRemainingShares() throws Exception {
        Sale partialSale = TransactionFactory.createSale(share, new BigDecimal("4"), 1);
        BigDecimal initialMoney = player.getMoney();

        partialSale.execute(player);

        assertTrue(partialSale.isCommitted());
        assertEquals(0, partialSale.getShare().getQuantity().compareTo(new BigDecimal("4")));
        assertEquals(1, player.getPortfolio().getShares().size());
        assertEquals(0, player.getPortfolio().getShares().get(0).getQuantity().compareTo(new BigDecimal("6")));
        assertEquals(0, player.getPortfolio().getShares().get(0).getPurchasePrice().compareTo(new BigDecimal("140.00")));
        assertEquals(0, player.getMoney().compareTo(initialMoney.add(new BigDecimal("583.80"))));
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("createSale: rejects quantity above owned amount")
    @Test
    void createSale_whenQuantityExceedsHolding_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> TransactionFactory.createSale(share, new BigDecimal("11"), 1));
    }

    @DisplayName("execute: throws exception when share not in portfolio")
    @Test
    void execute_whenShareNotOwned_throwsShareNotOwnedException() {
        Player emptyPlayer = new Player("EmptyPlayer", new BigDecimal("5000.00"));
        assertThrows(ShareNotOwnedException.class, () -> sale.execute(emptyPlayer));
        assertFalse(sale.isCommitted());
    }

    @DisplayName("execute: throws exception when already executed")
    @Test
    void execute_whenAlreadyExecuted_throwsTransactionAlreadyCommittedException() throws Exception {
        sale.execute(player);
        assertThrows(TransactionAlreadyCommittedException.class,
                () -> sale.execute(player));
    }

    @DisplayName("execute: throws NullPointerException when player is null")
    @Test
    void execute_whenPlayerIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> sale.execute(null));
    }

    @DisplayName("isCommitted: returns false before execute")
    @Test
    void isCommitted_whenNotExecuted_returnsFalse() {
        assertFalse(sale.isCommitted());
    }

    @DisplayName("isCommitted: returns true after execute")
    @Test
    void isCommitted_whenExecuted_returnsTrue() throws Exception {
        sale.execute(player);
        assertTrue(sale.isCommitted());
    }

    @DisplayName("getWeek: returns correct week")
    @Test
    void testGetWeek() {
        assertEquals(1, sale.getWeek());
    }

    @DisplayName("getShare: returns correct share")
    @Test
    void testGetShare() {
        assertEquals(share, sale.getShare());
    }
}
