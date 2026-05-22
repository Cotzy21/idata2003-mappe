package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when stock data cannot be parsed or validated.
 */
public class InvalidStockDataException extends Exception {

    /**
     * Constructs an {@code InvalidStockDataException} with a message.
     *
     * @param message the error message
     */
    public InvalidStockDataException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code InvalidStockDataException} with a message and cause.
     *
     * @param message the error message
     * @param cause the original cause
     */
    public InvalidStockDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
