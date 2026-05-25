package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when attempting to sell a share the player does not own.
 */
public class ShareNotOwnedException extends Exception {

    /**
     * Constructs a {@code ShareNotOwnedException} with a message.
     *
     * @param message the error message
     */
    public ShareNotOwnedException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code ShareNotOwnedException} with a message and cause.
     *
     * @param message the error message
     * @param cause the cause
     */
    public ShareNotOwnedException(String message, Throwable cause) {
        super(message, cause);
    }
}
