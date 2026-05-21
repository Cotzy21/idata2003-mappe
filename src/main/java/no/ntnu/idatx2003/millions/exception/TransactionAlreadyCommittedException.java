package no.ntnu.idatx2003.millions.exception;

/**
 * Exception thrown when attempting to commit an already-committed transaction.
 */
public class TransactionAlreadyCommittedException extends Exception {
    /**
     * Constructs a TransactionAlreadyCommittedException with a message.
     *
     * @param message the error message
     */
    public TransactionAlreadyCommittedException(String message) {
        super(message);
    }

    /**
     * Constructs a TransactionAlreadyCommittedException with a message and cause.
     *
     * @param message the error message
     * @param cause the cause
     */
    public TransactionAlreadyCommittedException(String message, Throwable cause) {
        super(message, cause);
    }
}

