package no.ntnu.idatx2003.millions.io;

import java.math.BigDecimal;

/**
 * DTO for a portfolio share in a saved game.
 *
 * @param symbol stock symbol
 * @param quantity share quantity
 * @param purchasePrice purchase price per share
 */
public record ShareDto(String symbol, BigDecimal quantity, BigDecimal purchasePrice) {
}
