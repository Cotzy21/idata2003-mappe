package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.exception.ShareNotInPortfolioException;
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

    @DisplayName("commit: successfully sells shares when present in portfolio")
    @Test
    void testCommit_success() throws Exception {
        BigDecimal initialMoney = player.getMoney();
        sale.commit(player);

        assertTrue(sale.isCommitted());
        // Money should be increased (1500.00 - 15.00 - 25.50 = 1459.50)
        BigDecimal expectedMoney = initialMoney.add(new BigDecimal("1459.50"));
        assertEquals(0, player.getMoney().compareTo(expectedMoney));
        // Share should be removed from portfolio
        assertEquals(0, player.getPortfolio().getShares().size());
        // Transaction should be in archive
        assertEquals(1, player.getTransactionArchive().getTransactions().size());
    }

    @DisplayName("commit: throws exception when share not in portfolio")
    @Test
    void testCommit_shareNotInPortfolio() {
        Player emptyPlayer = new Player("EmptyPlayer", new BigDecimal("5000.00"));
        assertThrows(ShareNotInPortfolioException.class, () -> sale.commit(emptyPlayer));
        assertFalse(sale.isCommitted());
    }

    @DisplayName("commit: throws exception when already committed")
    @Test
    void testCommit_alreadyCommitted() throws Exception {
        sale.commit(player);
        assertThrows(TransactionAlreadyCommittedException.class,
                () -> sale.commit(player));
    }

    @DisplayName("isCommitted: returns false before commit")
    @Test
    void testIsCommitted_false() {
        assertFalse(sale.isCommitted());
    }

    @DisplayName("isCommitted: returns true after commit")
    @Test
    void testIsCommitted_true() throws Exception {
        sale.commit(player);
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

