package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.exception.InsufficientFundsException;
import no.ntnu.idatx2003.millions.exception.TransactionAlreadyCommittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Purchase Transaction Tests")
class PurchaseTest {
    private Purchase purchase;
    private Stock stock;
    private Share share;
    private Player player;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        purchase = TransactionFactory.createPurchase(share, 1);
        player = new Player("TestPlayer", new BigDecimal("5000.00"));
    }

    @DisplayName("execute: successfully purchases shares when sufficient funds")
    @Test
    void execute_whenSufficientFunds_completesPurchase() throws Exception {
        BigDecimal initialMoney = player.getMoney();
        purchase.execute(player);

        assertTrue(purchase.isCommitted());
        assertTrue(purchase.isExecuted());
        // Money should be deducted (1480.00 + 7.40 = 1487.40)
        BigDecimal expectedMoney = initialMoney.subtract(new BigDecimal("1487.40"));
        assertEquals(0, player.getMoney().compareTo(expectedMoney));
        // Share should be in portfolio
        assertEquals(1, player.getPortfolio().getShares().size());
        // Transaction should be in archive
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("execute: throws InsufficientFundsException when insufficient funds")
    @Test
    void execute_whenInsufficientFunds_throwsInsufficientFundsException() {
        Player poorPlayer = new Player("PoorPlayer", new BigDecimal("100.00"));
        assertThrows(InsufficientFundsException.class, () -> purchase.execute(poorPlayer));
        assertFalse(purchase.isCommitted());
    }

    @DisplayName("execute: throws exception when already executed")
    @Test
    void execute_whenAlreadyExecuted_throwsTransactionAlreadyCommittedException() throws Exception {
        purchase.execute(player);
        assertThrows(TransactionAlreadyCommittedException.class,
                () -> purchase.execute(player));
    }

    @DisplayName("execute: throws NullPointerException when player is null")
    @Test
    void execute_whenPlayerIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> purchase.execute(null));
    }

    @DisplayName("isCommitted: returns false before execute")
    @Test
    void isCommitted_whenNotExecuted_returnsFalse() {
        assertFalse(purchase.isCommitted());
    }

    @DisplayName("isCommitted: returns true after execute")
    @Test
    void isCommitted_whenExecuted_returnsTrue() throws Exception {
        purchase.execute(player);
        assertTrue(purchase.isCommitted());
    }

    @DisplayName("getWeek: returns correct week")
    @Test
    void testGetWeek() {
        assertEquals(1, purchase.getWeek());
    }

    @DisplayName("getShare: returns correct share")
    @Test
    void testGetShare() {
        assertEquals(share, purchase.getShare());
    }
}

