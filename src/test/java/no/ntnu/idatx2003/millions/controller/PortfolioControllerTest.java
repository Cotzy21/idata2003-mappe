package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortfolioControllerTest {

    @Test
    void liquidate_whenPlayerHasThreeShares_returnsThreeTransactionsAndEmptiesPortfolio() throws Exception {
        Stock apple = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        Stock microsoft = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
        Stock nvidia = new Stock("NVDA", "Nvidia", new BigDecimal("900.00"));
        Exchange exchange = new Exchange("Test Exchange", List.of(apple, microsoft, nvidia));
        Player player = new Player("Trader", new BigDecimal("10000.00"));
        player.getPortfolio().addShare(new Share(apple, BigDecimal.ONE, new BigDecimal("140.00")));
        player.getPortfolio().addShare(new Share(microsoft, BigDecimal.ONE, new BigDecimal("290.00")));
        player.getPortfolio().addShare(new Share(nvidia, BigDecimal.ONE, new BigDecimal("850.00")));

        List<Transaction> transactions = PortfolioController.liquidate(exchange, player);

        assertEquals(3, transactions.size());
        assertEquals(0, player.getPortfolio().getShares().size());
        assertEquals(3, player.getTransactionArchive().getTransactions().size());
    }
}
