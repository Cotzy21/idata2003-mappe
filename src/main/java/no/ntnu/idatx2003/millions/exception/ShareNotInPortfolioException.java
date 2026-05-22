package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when attempting to sell a share that is not in the player's portfolio.
 */
public class ShareNotInPortfolioException extends Exception {
    /**
     * Constructs a ShareNotInPortfolioException with a message.
     *
     * @param message the error message
     */
    public ShareNotInPortfolioException(String message) {
        super(message);
    }

    /**
     * Constructs a ShareNotInPortfolioException with a message and cause.
     *
     * @param message the error message
     * @param cause the cause
     */
    public ShareNotInPortfolioException(String message, Throwable cause) {
        super(message, cause);
    }
}

