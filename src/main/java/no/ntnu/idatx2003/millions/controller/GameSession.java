package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mutable container for the active game state shared between the controllers.
 */
final class GameSession {
    private static final String DEFAULT_PLAYER_NAME = "Trader";

    private String playerName;
    private BigDecimal startingMoney;
    private Exchange exchange;
    private Player player;

    GameSession(BigDecimal startingMoney) {
        this(DEFAULT_PLAYER_NAME, startingMoney);
    }

    GameSession(String playerName, BigDecimal startingMoney) {
        this.playerName = Validate.requireNotBlank(playerName, "playerName");
        this.startingMoney = Validate.requireNonNull(startingMoney, "startingMoney");
    }

    void reset(List<Stock> stocks) {
        Validate.requireNonNull(stocks, "stocks");
        exchange = new Exchange("Millions Exchange", stocks);
        player = new Player(playerName, startingMoney);
    }

    void load(Player player, Exchange exchange) {
        this.player = Validate.requireNonNull(player, "player");
        this.exchange = Validate.requireNonNull(exchange, "exchange");
        this.playerName = player.getName();
        this.startingMoney = player.getStartingMoney();
    }

    String playerName() {
        return playerName;
    }

    void setPlayerName(String playerName) {
        this.playerName = Validate.requireNotBlank(playerName, "playerName");
    }

    BigDecimal startingMoney() {
        return startingMoney;
    }

    void setStartingMoney(BigDecimal startingMoney) {
        this.startingMoney = Validate.requireNonNull(startingMoney, "startingMoney");
    }

    Exchange exchange() {
        return exchange;
    }

    Player player() {
        return player;
    }
}
