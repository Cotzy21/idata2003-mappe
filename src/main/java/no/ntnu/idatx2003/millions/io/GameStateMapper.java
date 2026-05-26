package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Player;
import no.ntnu.idatx2003.millions.model.Share;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.transaction.Purchase;
import no.ntnu.idatx2003.millions.model.transaction.Sale;
import no.ntnu.idatx2003.millions.model.transaction.Transaction;
import no.ntnu.idatx2003.millions.model.transaction.TransactionFactory;
import no.ntnu.idatx2003.millions.util.Validate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps between domain objects and JSON-friendly game-state DTOs.
 */
public final class GameStateMapper {
    private static final String PURCHASE = "PURCHASE";
    private static final String SALE = "SALE";

    private GameStateMapper() {
        // Utility class.
    }

    /**
     * Converts the current domain state to a serializable DTO.
     *
     * @param player the player to save; must not be {@code null}
     * @param exchange the exchange to save; must not be {@code null}
     * @return a serializable game state
     */
    public static GameState toGameState(Player player, Exchange exchange) {
        Validate.requireNonNull(player, "player");
        Validate.requireNonNull(exchange, "exchange");
        return new GameState(
                player.getName(),
                player.getStartingMoney(),
                player.getMoney(),
                player.getPortfolio().getShares().stream()
                        .map(GameStateMapper::toShareDto)
                        .toList(),
                player.getTransactionArchive().getTransactions().stream()
                        .map(GameStateMapper::toTransactionDto)
                        .toList(),
                exchange.getAllStocks().stream()
                        .map(GameStateMapper::toStockDto)
                        .toList(),
                exchange.getWeek(),
                exchange.getName());
    }

    /**
     * Recreates domain objects from a saved DTO.
     *
     * @param gameState the saved state; must not be {@code null}
     * @return materialized player and exchange
     */
    public static MaterializedGameState fromGameState(GameState gameState) {
        Validate.requireNonNull(gameState, "gameState");
        List<Stock> stocks = gameState.stocks().stream()
                .map(GameStateMapper::toStock)
                .toList();
        Map<String, Stock> stocksBySymbol = stocks.stream()
                .collect(Collectors.toUnmodifiableMap(Stock::getSymbol, Function.identity()));

        Exchange exchange = new Exchange(gameState.exchangeName(), stocks, gameState.week());
        Player player = new Player(gameState.playerName(), gameState.startingMoney());
        setPlayerMoney(player, gameState.currentMoney());
        gameState.shares().stream()
                .map(shareDto -> toShare(shareDto, stocksBySymbol))
                .forEach(player.getPortfolio()::addShare);
        gameState.transactions().stream()
                .map(transactionDto -> toTransaction(transactionDto, stocksBySymbol))
                .forEach(player.getTransactionArchive()::add);
        return new MaterializedGameState(player, exchange);
    }

    private static ShareDto toShareDto(Share share) {
        return new ShareDto(
                share.getStock().getSymbol(),
                share.getQuantity(),
                share.getPurchasePrice());
    }

    private static TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transactionType(transaction),
                transaction.getWeek(),
                transaction.getShare().getStock().getSymbol(),
                transaction.getShare().getQuantity(),
                transaction.getShare().getPurchasePrice());
    }

    private static StockDto toStockDto(Stock stock) {
        return new StockDto(stock.getSymbol(), stock.getCompany(), stock.getHistoricalPrices());
    }

    private static Stock toStock(StockDto stockDto) {
        Validate.requireNonNull(stockDto, "stockDto");
        if (stockDto.prices() == null || stockDto.prices().isEmpty()) {
            throw new IllegalArgumentException("Saved stock must contain at least one price");
        }
        Stock stock = new Stock(stockDto.symbol(), stockDto.company(), stockDto.prices().getFirst());
        stockDto.prices().stream()
                .skip(1)
                .forEach(stock::addNewSalesPrice);
        return stock;
    }

    private static Share toShare(ShareDto shareDto, Map<String, Stock> stocksBySymbol) {
        Stock stock = stockForSymbol(shareDto.symbol(), stocksBySymbol);
        return new Share(stock, shareDto.quantity(), shareDto.purchasePrice());
    }

    private static Transaction toTransaction(TransactionDto transactionDto, Map<String, Stock> stocksBySymbol) {
        Share share = new Share(
                stockForSymbol(transactionDto.symbol(), stocksBySymbol),
                transactionDto.quantity(),
                transactionDto.purchasePrice());
        return switch (transactionDto.type()) {
            case PURCHASE -> TransactionFactory.createPurchase(share, transactionDto.week());
            case SALE -> TransactionFactory.createSale(share, transactionDto.week());
            default -> throw new IllegalArgumentException("Unknown transaction type: " + transactionDto.type());
        };
    }

    private static Stock stockForSymbol(String symbol, Map<String, Stock> stocksBySymbol) {
        Stock stock = stocksBySymbol.get(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("Saved state references unknown stock: " + symbol);
        }
        return stock;
    }

    private static String transactionType(Transaction transaction) {
        if (transaction instanceof Purchase) {
            return PURCHASE;
        }
        if (transaction instanceof Sale) {
            return SALE;
        }
        throw new IllegalArgumentException("Unsupported transaction type: " + transaction.getClass().getName());
    }

    private static void setPlayerMoney(Player player, BigDecimal currentMoney) {
        Validate.requireNonNull(currentMoney, "currentMoney");
        BigDecimal difference = currentMoney.subtract(player.getMoney());
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            player.addMoney(difference);
        } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
            player.withdrawMoney(difference.abs());
        }
    }

    /**
     * Domain objects recreated from a saved game state.
     *
     * @param player materialized player
     * @param exchange materialized exchange
     */
    public record MaterializedGameState(Player player, Exchange exchange) {
    }
}
