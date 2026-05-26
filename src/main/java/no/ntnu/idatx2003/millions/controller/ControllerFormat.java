package no.ntnu.idatx2003.millions.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class ControllerFormat {

    private ControllerFormat() {
        // Utility class.
    }

    static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    static String signedMoney(BigDecimal amount) {
        String value = money(amount);
        return amount.compareTo(BigDecimal.ZERO) > 0 ? "+" + value : value;
    }

    static String quantity(BigDecimal quantity) {
        return quantity.stripTrailingZeros().toPlainString();
    }
}
