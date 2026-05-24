package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

final class GameSession {
    private BigDecimal startingMoney;
    private Exchange exchange;
    private Player player;

    GameSession(BigDecimal startingMoney) {
        this.startingMoney = Objects.requireNonNull(startingMoney, "startingMoney must not be null");
    }

    void reset(List<Stock> stocks) {
        exchange = new Exchange("Millions Exchange", stocks);
        player = new Player("Player", startingMoney);
    }

    BigDecimal startingMoney() {
        return startingMoney;
    }

    void setStartingMoney(BigDecimal startingMoney) {
        this.startingMoney = Objects.requireNonNull(startingMoney, "startingMoney must not be null");
    }

    Exchange exchange() {
        return exchange;
    }

    Player player() {
        return player;
    }
}
