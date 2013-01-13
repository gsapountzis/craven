package craven.transaction;

/**
 * The SystemException is thrown to indicate that the transaction manager has
 * encountered an unexpected error condition that prevents future transaction
 * services from proceeding.
 */
public class SystemException extends TransactionException {

    public SystemException() {
        super();
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

}
