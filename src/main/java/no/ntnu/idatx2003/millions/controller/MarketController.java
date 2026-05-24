package no.ntnu.idatx2003.millions.controller;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.io.StockDataWriter;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.view.MainView;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * Handles market actions and selected-stock presentation.
 */
public class MarketController {
    private final MainView view;
    private final GameSession session;
    private final StockDataReader stockDataReader;
    private final StockDataWriter stockDataWriter;
    private final Runnable refreshAll;
    private final BiConsumer<List<Stock>, String> resetGame;

    /**
     * Creates a market controller.
     *
     * @param view the view to update; must not be {@code null}
     * @param session the game session; must not be {@code null}
     * @param stockDataReader the stock reader strategy; must not be {@code null}
     * @param stockDataWriter the stock writer strategy; must not be {@code null}
     * @param refreshAll callback for refreshing the full view; must not be {@code null}
     * @param resetGame callback for resetting the game; must not be {@code null}
     */
    public MarketController(MainView view, GameSession session, StockDataReader stockDataReader,
                            StockDataWriter stockDataWriter, Runnable refreshAll,
                            BiConsumer<List<Stock>, String> resetGame) {
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.session = Objects.requireNonNull(session, "session must not be null");
        this.stockDataReader = Objects.requireNonNull(stockDataReader, "stockDataReader must not be null");
        this.stockDataWriter = Objects.requireNonNull(stockDataWriter, "stockDataWriter must not be null");
        this.refreshAll = Objects.requireNonNull(refreshAll, "refreshAll must not be null");
        this.resetGame = Objects.requireNonNull(resetGame, "resetGame must not be null");
    }

    /**
     * Buys the selected stock using the quantity from the view.
     */
    public void buySelectedStock() {
        Stock stock = view.getSelectedStock();
        if (stock == null) {
            view.showMessage("Select a stock before buying.", true);
            return;
        }

        QuantityParser.parse(view.getQuantityText(), this::showQuantityError)
                .ifPresent(quantity -> buy(stock, quantity));
    }

    /**
     * Advances the exchange by one week.
     */
    public void advanceWeek() {
        session.exchange().advance();
        view.showMessage("Advanced to week " + session.exchange().getWeek() + ".", false);
    }

    /**
     * Loads stocks from a user-selected CSV file.
     */
    public void loadStocksFromCsv() {
        File file = view.chooseCsvFileToLoad();
        if (file == null) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<Stock> stocks = stockDataReader.read(reader);
            if (stocks.isEmpty()) {
                view.showMessage("CSV file contains no stocks.", true);
                return;
            }
            resetGame.accept(stocks, "Loaded " + stocks.size() + " stocks from " + file.getName() + ".");
        } catch (IOException | InvalidStockDataException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Saves current exchange stocks to a user-selected CSV file.
     */
    public void saveStocksToCsv() {
        File file = view.chooseCsvFileToSave("stocks-week-" + session.exchange().getWeek() + ".csv");
        if (file == null) {
            return;
        }

        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            stockDataWriter.write(writer, session.exchange().getAllStocks());
            view.showMessage("Saved stocks to " + file.getName() + ".", false);
        } catch (IOException exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    /**
     * Updates selected stock details.
     *
     * @param stock the selected stock, or {@code null}
     */
    public void showStockDetails(Stock stock) {
        if (stock == null) {
            view.clearSelectedStockDetails();
            return;
        }
        view.setSelectedStockDetails(stock.getSymbol() + " " + stock.getCompany()
                + " | price " + ControllerFormat.money(stock.getSalesPrice())
                + " | latest change " + ControllerFormat.signedMoney(stock.getLatestPriceChange())
                + " | high " + ControllerFormat.money(stock.getHighestPrice())
                + " | low " + ControllerFormat.money(stock.getLowestPrice()), createPricePoints(stock));
    }

    /**
     * Updates market statistics in the view.
     */
    public void updateMarketStats() {
        List<Stock> gainers = session.exchange().getGainers(1);
        List<Stock> losers = session.exchange().getLosers(1);
        if (gainers.isEmpty() || losers.isEmpty()) {
            view.setMarketStats("No market statistics available.");
            return;
        }
        Stock gainer = gainers.getFirst();
        Stock loser = losers.getFirst();
        view.setMarketStats("Top gainer: " + gainer.getSymbol() + " "
                + ControllerFormat.signedMoney(gainer.getLatestPriceChange())
                + " | Top loser: " + loser.getSymbol() + " "
                + ControllerFormat.signedMoney(loser.getLatestPriceChange()));
    }

    private void buy(Stock stock, BigDecimal quantity) {
        try {
            session.exchange().buy(stock.getSymbol(), quantity, session.player());
            view.showMessage("Bought " + ControllerFormat.quantity(quantity) + " " + stock.getSymbol() + ".", false);
            refreshAll.run();
        } catch (Exception exception) {
            view.showMessage(exception.getMessage(), true);
        }
    }

    private void showQuantityError(String message) {
        view.showMessage(message, true);
    }

    private List<MainView.PricePoint> createPricePoints(Stock stock) {
        List<BigDecimal> prices = stock.getHistoricalPrices();
        return IntStream.range(0, prices.size())
                .mapToObj(index -> createPricePoint(prices, index))
                .toList()
                .reversed();
    }

    private MainView.PricePoint createPricePoint(List<BigDecimal> prices, int index) {
        BigDecimal previous = index == 0 ? prices.get(index) : prices.get(index - 1);
        return new MainView.PricePoint(index + 1, prices.get(index), prices.get(index).subtract(previous));
    }
}
