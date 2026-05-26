package no.ntnu.idatx2003.millions.view;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Outcome of {@link NewGameDialog}: the player's chosen name, starting
 * capital, the resolved stock list (either parsed from a CSV file or the
 * built-in defaults) and the source file when one was picked.
 *
 * @param playerName the player's chosen display name
 * @param startingMoney the initial cash balance
 * @param stocks the resolved stock list, never {@code null}
 * @param sourceFile the CSV file that was loaded, or {@code null} when defaults were used
 */
public record NewGameResult(String playerName,
                            BigDecimal startingMoney,
                            List<Stock> stocks,
                            File sourceFile) {
}
