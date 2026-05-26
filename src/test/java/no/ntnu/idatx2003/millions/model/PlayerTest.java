package no.ntnu.idatx2003.millions.model;

import no.ntnu.idatx2003.millions.model.transaction.TransactionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Player Tests")
class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Alice", new BigDecimal("10000.00"));
    }

    @DisplayName("Constructor: creates player with valid inputs")
    @Test
    void testConstructor_valid() {
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getStartingMoney().compareTo(new BigDecimal("10000.00")));
        assertEquals(0, player.getMoney().compareTo(new BigDecimal("10000.00")));
    }

    @DisplayName("Constructor: throws exception for null name")
    @Test
    void testConstructor_nullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(null, new BigDecimal("10000")));
    }

    @DisplayName("Constructor: throws exception for blank name")
    @Test
    void testConstructor_blankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("", new BigDecimal("10000")));
    }

    @Test
    void constructor_whenNameIsBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("   ", new BigDecimal("10000")));
    }

    @DisplayName("Constructor: throws exception for negative starting money")
    @Test
    void testConstructor_negativeMoney() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("Alice", new BigDecimal("-100")));
    }

    @Test
    void constructor_whenStartingMoneyIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("Alice", new BigDecimal("-0.01")));
    }

    @DisplayName("addMoney: increases current money")
    @Test
    void testAddMoney() {
        player.addMoney(new BigDecimal("5000.00"));
        assertEquals(0, player.getMoney().compareTo(new BigDecimal("15000.00")));
    }

    @DisplayName("addMoney: throws exception for negative amount")
    @Test
    void testAddMoney_negative() {
        assertThrows(IllegalArgumentException.class,
                () -> player.addMoney(new BigDecimal("-100")));
    }

    @DisplayName("withdrawMoney: decreases current money")
    @Test
    void testWithdrawMoney() {
        player.withdrawMoney(new BigDecimal("2000.00"));
        assertEquals(0, player.getMoney().compareTo(new BigDecimal("8000.00")));
    }

    @DisplayName("withdrawMoney: throws exception when insufficient funds")
    @Test
    void testWithdrawMoney_insufficient() {
        assertThrows(IllegalArgumentException.class,
                () -> player.withdrawMoney(new BigDecimal("20000.00")));
    }

    @Test
    void withdrawMoney_whenAmountExceedsBalance_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> player.withdrawMoney(new BigDecimal("10000.01")));
    }

    @DisplayName("withdrawMoney: throws exception for negative amount")
    @Test
    void testWithdrawMoney_negative() {
        assertThrows(IllegalArgumentException.class,
                () -> player.withdrawMoney(new BigDecimal("-100")));
    }

    @Test
    void withdrawMoney_whenAmountIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> player.withdrawMoney(new BigDecimal("-0.01")));
    }

    @DisplayName("getNetWorth: returns money + portfolio value")
    @Test
    void testGetNetWorth() {
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        player.getPortfolio().addShare(share);

        // 10000.00 + net sale value 1483.50 = 11483.50
        BigDecimal netWorth = player.getNetWorth();
        assertEquals(0, netWorth.compareTo(new BigDecimal("11483.50")));
    }

    @DisplayName("getStatus: returns NOVICE for new player")
    @Test
    void testGetStatus_novice() {
        assertEquals(PlayerStatus.NOVICE, player.getStatus());
    }

    @Test
    void getStatus_whenZeroWeeks_returnsNovice() {
        player.addMoney(new BigDecimal("10000.00"));

        assertEquals(PlayerStatus.NOVICE, player.getStatus());
    }

    @DisplayName("getStatus: returns INVESTOR when conditions met")
    @Test
    void testGetStatus_investor() {
        // Add transaction archive entries for 10 weeks
        for (int i = 1; i <= 10; i++) {
            Stock stock = new Stock("TEST" + i, "Test" + i, new BigDecimal("100.00"));
            Share share = new Share(stock, BigDecimal.ONE, new BigDecimal("100.00"));
            player.getTransactionArchive().add(
                    TransactionFactory.createPurchase(share, i)
            );
        }

        // Add enough money/portfolio to reach 1.2x start
        player.addMoney(new BigDecimal("2000.00"));

        assertEquals(PlayerStatus.INVESTOR, player.getStatus());
    }

    @DisplayName("getStatus: returns SPECULATOR when conditions met")
    @Test
    void testGetStatus_speculator() {
        // Add transaction archive entries for 20 weeks
        for (int i = 1; i <= 20; i++) {
            Stock stock = new Stock("TEST" + i, "Test" + i, new BigDecimal("100.00"));
            Share share = new Share(stock, BigDecimal.ONE, new BigDecimal("100.00"));
            player.getTransactionArchive().add(
                    TransactionFactory.createPurchase(share, i)
            );
        }

        // Add enough money to reach 2x start (20000.00 / 10000.00 = 2x)
        player.addMoney(new BigDecimal("10000.00"));

        assertEquals(PlayerStatus.SPECULATOR, player.getStatus());
    }

    @Test
    void getStatus_whenBothSpeculatorAndInvestorMatch_returnsSpeculator() {
        for (int week = 1; week <= 20; week++) {
            Stock stock = new Stock("SPEC" + week, "Spec " + week, new BigDecimal("100.00"));
            Share share = new Share(stock, BigDecimal.ONE, new BigDecimal("100.00"));
            player.getTransactionArchive().add(TransactionFactory.createPurchase(share, week));
        }
        player.addMoney(new BigDecimal("10000.00"));

        assertEquals(PlayerStatus.SPECULATOR, player.getStatus());
    }

    @DisplayName("Getters: return correct values")
    @Test
    void testGetters() {
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getStartingMoney().compareTo(new BigDecimal("10000.00")));
        assertNotNull(player.getPortfolio());
        assertNotNull(player.getTransactionArchive());
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Object is null");
        }
    }
}
