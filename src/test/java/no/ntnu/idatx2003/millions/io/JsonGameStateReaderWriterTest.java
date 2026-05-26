package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonGameStateReaderWriterTest {

    @Test
    void writeAndRead_whenStateHasPortfolioTransactionsAndWeek5_roundTripsGameState() throws Exception {
        Exchange exchange = new Exchange("Test Exchange", List.of(
                new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00")),
                new Stock("MSFT", "Microsoft", new BigDecimal("300.00")),
                new Stock("NVDA", "Nvidia", new BigDecimal("900.00"))));
        Player player = new Player("Trader", new BigDecimal("100000.00"));
        for (int i = 0; i < 4; i++) {
            exchange.advance();
        }
        exchange.buy("AAPL", new BigDecimal("2"), player);
        exchange.buy("MSFT", new BigDecimal("1"), player);
        exchange.sell(player.getPortfolio().getShares().getFirst(), BigDecimal.ONE, player);

        GameState original = GameStateMapper.toGameState(player, exchange);
        StringWriter output = new StringWriter();

        new JsonGameStateWriter().write(output, original);
        GameState loaded = new JsonGameStateReader().read(new StringReader(output.toString()));
        GameStateMapper.MaterializedGameState materialized = GameStateMapper.fromGameState(loaded);

        assertEquals(original, loaded);
        assertEquals(5, materialized.exchange().getWeek());
        assertEquals(3, materialized.exchange().getAllStocks().size());
        assertEquals(2, materialized.player().getPortfolio().getShares().size());
        assertEquals(3, materialized.player().getTransactionArchive().getTransactions().size());
        assertEquals(0, player.getMoney().compareTo(materialized.player().getMoney()));
    }
}
