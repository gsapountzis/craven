package craven.transaction;

/**
 * The RollbackException exception indicates that either (a) the transaction has
 * been rolled back instead of committed or (b) an operation cannot complete
 * because the transaction has been marked for rollback only.
 */
public class RollbackException extends TransactionException {

    public RollbackException() {
        super();
    }

    public RollbackException(String message) {
        super(message);
    }

    public RollbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollbackException(Throwable cause) {
        super(cause);
    }

}
