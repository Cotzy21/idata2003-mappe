package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when a player has insufficient funds for a transaction.
 */
public class InsufficientFundsException extends Exception {
    /**
     * Constructs an InsufficientFundsException with a message.
     *
     * @param message the error message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructs an InsufficientFundsException with a message and cause.
     *
     * @param message the error message
     * @param cause the cause
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}

