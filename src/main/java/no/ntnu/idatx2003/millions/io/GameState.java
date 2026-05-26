package no.ntnu.idatx2003.millions.io;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing a complete saved game state.
 *
 * @param playerName player display name
 * @param startingMoney original starting capital
 * @param currentMoney current cash balance
 * @param shares portfolio rows
 * @param transactions archived transactions
 * @param stocks exchange stocks
 * @param week current exchange week
 * @param exchangeName exchange display name
 */
public record GameState(String playerName,
                        BigDecimal startingMoney,
                        BigDecimal currentMoney,
                        List<ShareDto> shares,
                        List<TransactionDto> transactions,
                        List<StockDto> stocks,
                        int week,
                        String exchangeName) {
}
