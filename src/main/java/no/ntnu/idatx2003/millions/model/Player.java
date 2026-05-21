package no.ntnu.idatx2003.millions.model;

import no.ntnu.idatx2003.millions.model.transaction.TransactionArchive;

import java.math.BigDecimal;

/**
 * Represents a player in the Millions game.
 * Tracks player name, money, portfolio, and transaction history.
 */
public class Player {
    private final String name;
    private final BigDecimal startingMoney;
    private BigDecimal money;
    private final Portfolio portfolio;
    private final TransactionArchive transactionArchive;

    /**
     * Constructs a Player with starting conditions.
     *
     * @param name the player's name
     * @param startingMoney the initial amount of money
     * @throws IllegalArgumentException if name is empty or money is negative
     */
    public Player(String name, BigDecimal startingMoney) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (startingMoney == null || startingMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Starting money must be non-negative");
        }

        this.name = name;
        this.startingMoney = startingMoney;
        this.money = startingMoney;
        this.portfolio = new Portfolio();
        this.transactionArchive = new TransactionArchive();
    }

    /**
     * Gets the player's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's starting money.
     *
     * @return the starting amount
     */
    public BigDecimal getStartingMoney() {
        return startingMoney;
    }

    /**
     * Gets the player's current money.
     *
     * @return the current cash balance
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * Adds money to the player's account.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void addMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        money = money.add(amount);
    }

    /**
     * Withdraws money from the player's account.
     *
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if amount is negative or exceeds balance
     */
    public void withdrawMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (money.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds for withdrawal. Available: " + money +
                            ", Requested: " + amount);
        }
        money = money.subtract(amount);
    }

    /**
     * Gets the player's portfolio.
     *
     * @return the Portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Gets the player's transaction archive.
     *
     * @return the TransactionArchive
     */
    public TransactionArchive getTransactionArchive() {
        return transactionArchive;
    }

    /**
     * Calculates the player's net worth (cash + portfolio value).
     *
     * @return the total net worth
     */
    public BigDecimal getNetWorth() {
        return money.add(portfolio.getNetWorth());
    }

    /**
     * Determines the player's status based on trading activity and net worth.
     * Check SPECULATOR first, then INVESTOR, then default to NOVICE.
     *
     * @return the PlayerStatus
     */
    public PlayerStatus getStatus() {
        int distinctWeeks = transactionArchive.countDistinctWeeks();
        BigDecimal netWorth = getNetWorth();

        // SPECULATOR: traded ≥20 weeks AND net worth ≥ 2x starting money
        if (distinctWeeks >= 20 && netWorth.compareTo(startingMoney.multiply(new BigDecimal("2"))) >= 0) {
            return PlayerStatus.SPECULATOR;
        }

        // INVESTOR: traded ≥10 weeks AND net worth ≥ 1.2x starting money
        if (distinctWeeks >= 10 && netWorth.compareTo(startingMoney.multiply(new BigDecimal("1.2"))) >= 0) {
            return PlayerStatus.INVESTOR;
        }

        // NOVICE: default
        return PlayerStatus.NOVICE;
    }

    @Override
    public String toString() {
        return String.format("Player: %s, Money: %s, Status: %s, Net Worth: %s",
                name, money, getStatus(), getNetWorth());
    }
}

