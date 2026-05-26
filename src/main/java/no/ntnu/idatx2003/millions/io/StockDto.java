package no.ntnu.idatx2003.millions.io;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for a stock in a saved game.
 *
 * @param symbol stock symbol
 * @param company company name
 * @param prices historical prices in chronological order
 */
public record StockDto(String symbol, String company, List<BigDecimal> prices) {
}
