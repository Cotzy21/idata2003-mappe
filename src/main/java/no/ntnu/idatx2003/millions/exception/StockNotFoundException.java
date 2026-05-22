package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when attempting to find a stock that doesn't exist in the exchange.
 */
public class StockNotFoundException extends Exception {
    /**
     * Constructs a StockNotFoundException with a message.
     *
     * @param message the error message
     */
    public StockNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a StockNotFoundException with a message and cause.
     *
     * @param message the error message
     * @param cause the cause
     */
    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

