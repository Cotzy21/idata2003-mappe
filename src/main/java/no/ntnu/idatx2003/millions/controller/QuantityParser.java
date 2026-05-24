package no.ntnu.idatx2003.millions.controller;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

final class QuantityParser {

    private QuantityParser() {
        // Utility class.
    }

    static Optional<BigDecimal> parse(String rawQuantity, Consumer<String> errorConsumer) {
        if (rawQuantity == null || rawQuantity.isBlank()) {
            errorConsumer.accept("Quantity must be filled in.");
            return Optional.empty();
        }

        try {
            BigDecimal quantity = new BigDecimal(rawQuantity.trim());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                errorConsumer.accept("Quantity must be greater than zero.");
                return Optional.empty();
            }
            return Optional.of(quantity);
        } catch (NumberFormatException exception) {
            errorConsumer.accept("Quantity must be a valid number.");
            return Optional.empty();
        }
    }
}
