package no.ntnu.idatx2003.millions.view;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formats values for display in JavaFX views.
 */
final class ViewFormat {

    private ViewFormat() {
        // Utility class.
    }

    /**
     * Formats a monetary value with two decimals.
     *
     * @param amount the amount to format
     * @return the formatted value
     */
    static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * Formats a monetary value and prefixes positive values with a plus sign.
     *
     * @param amount the amount to format
     * @return the formatted value
     */
    static String signedMoney(BigDecimal amount) {
        String value = money(amount);
        return amount.compareTo(BigDecimal.ZERO) > 0 ? "+" + value : value;
    }

    /**
     * Formats a quantity without unnecessary trailing zeroes.
     *
     * @param quantity the quantity to format
     * @return the formatted quantity
     */
    static String quantity(BigDecimal quantity) {
        return quantity.stripTrailingZeros().toPlainString();
    }
}
