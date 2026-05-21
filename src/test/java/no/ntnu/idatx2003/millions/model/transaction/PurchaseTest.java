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

    @DisplayName("commit: successfully purchases shares when sufficient funds")
    @Test
    void testCommit_success() throws Exception {
        BigDecimal initialMoney = player.getMoney();
        purchase.commit(player);

        assertTrue(purchase.isCommitted());
        // Money should be deducted (1480.00 + 7.40 = 1487.40)
        BigDecimal expectedMoney = initialMoney.subtract(new BigDecimal("1487.40"));
        assertEquals(0, player.getMoney().compareTo(expectedMoney));
        // Share should be in portfolio
        assertEquals(1, player.getPortfolio().getShares().size());
        // Transaction should be in archive
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("commit: throws InsufficientFundsException when insufficient funds")
    @Test
    void testCommit_insufficientFunds() {
        Player poorPlayer = new Player("PoorPlayer", new BigDecimal("100.00"));
        assertThrows(InsufficientFundsException.class, () -> purchase.commit(poorPlayer));
        assertFalse(purchase.isCommitted());
    }

    @DisplayName("commit: throws exception when already committed")
    @Test
    void testCommit_alreadyCommitted() throws Exception {
        purchase.commit(player);
        assertThrows(TransactionAlreadyCommittedException.class,
                () -> purchase.commit(player));
    }

    @DisplayName("isCommitted: returns false before commit")
    @Test
    void testIsCommitted_false() {
        assertFalse(purchase.isCommitted());
    }

    @DisplayName("isCommitted: returns true after commit")
    @Test
    void testIsCommitted_true() throws Exception {
        purchase.commit(player);
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

