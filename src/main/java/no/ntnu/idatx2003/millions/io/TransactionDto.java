package no.ntnu.idatx2003.millions.io;

import java.math.BigDecimal;

/**
 * DTO for a transaction archive row in a saved game.
 *
 * @param type transaction type, either {@code PURCHASE} or {@code SALE}
 * @param week transaction week
 * @param symbol stock symbol
 * @param quantity traded quantity
 * @param purchasePrice purchase price stored on the transaction share
 */
public record TransactionDto(String type,
                             int week,
                             String symbol,
                             BigDecimal quantity,
                             BigDecimal purchasePrice) {
}
