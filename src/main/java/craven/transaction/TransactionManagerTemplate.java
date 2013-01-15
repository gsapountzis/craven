package craven.transaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import craven.interceptor.Transactional;
import craven.interceptor.TxConfig;

/**
 * Transaction Manager implementation with the Template pattern, could also use the Strategy pattern.
 */
public abstract class TransactionManagerTemplate<ContextType extends TransactionContext> implements TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManagerTemplate.class);

    private final ThreadLocal<Status> status = new ThreadLocal<Status>() {
        @Override protected Status initialValue() {
            return Status.NO_TRANSACTION;
        }
    };

    private final ThreadLocal<ContextType> context = new ThreadLocal<ContextType>() {
        @Override protected ContextType initialValue() {
            return createTransactionContext();
        }
    };

    public TransactionManagerTemplate() {
    }

    @Override
    public void begin() throws IllegalStateException, SystemException {
        begin(TxConfig.DEFAULT);
    }

    @Override
    public void begin(Transactional annotation) throws IllegalStateException, SystemException {

        if (status.get() != Status.NO_TRANSACTION) {
            throw new IllegalStateException("Transaction is already active");
        }

        String thread = Thread.currentThread().getName();

        try {
            logger.debug("[{}] Beginning transaction", thread);
            doBeginTransaction(annotation);
            status.set(Status.ACTIVE);

            return;
        }
        catch (SystemException beginEx) {
            logger.debug("[{}] Could not begin transaction", thread, beginEx);
            status.set(Status.UNKNOWN);

            doEndTransaction();
            context.remove();
            status.remove();

            throw beginEx;
        }
    }

    @Override
    public void commit() throws IllegalStateException, RollbackException, SystemException {

        if (status.get() != Status.ACTIVE && status.get() != Status.MARKED_ROLLBACK) {
            throw new IllegalStateException("Transaction is not active");
        }

        String thread = Thread.currentThread().getName();

        if (status.get() == Status.MARKED_ROLLBACK) {
            try {
                logger.debug("[{}] Rolling back transaction (marked)", thread);
                doRollbackTransaction();
                status.set(Status.ROLLEDBACK);

                doEndTransaction();
                context.remove();
                status.remove();

                throw new RollbackException("Transaction was marked as rollback only");
            }
            catch (SystemException rollbackEx) {
                logger.debug("[{}] Could not rollback transaction", thread, rollbackEx);
                status.set(Status.UNKNOWN);

                doEndTransaction();
                context.remove();
                status.remove();

                throw rollbackEx;
            }
        }
        else {
            try {
                logger.debug("[{}] Committing transaction", thread);
                doCommitTransaction();
                status.set(Status.COMMITTED);

                doEndTransaction();
                context.remove();
                status.remove();

                return;
            }
            catch (SystemException commitEx) {
                logger.debug("[{}] Could not commit transaction", thread, commitEx);
                status.set(Status.UNKNOWN);

                try {
                    logger.debug("[{}] Rolling back transaction (exception)", thread);
                    doRollbackTransaction();
                    status.set(Status.ROLLEDBACK);

                    doEndTransaction();
                    context.remove();
                    status.remove();

                    throw new RollbackException("Transaction failed to commit", commitEx);
                }
                catch (SystemException rollbackEx) {
                    logger.debug("[{}] Could not rollback transaction", thread, rollbackEx);
                    status.set(Status.UNKNOWN);

                    doEndTransaction();
                    context.remove();
                    status.remove();

                    throw rollbackEx;
                }
            }
        }
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {

        if (status.get() != Status.ACTIVE && status.get() != Status.MARKED_ROLLBACK) {
            throw new IllegalStateException("Transaction is not active");
        }

        String thread = Thread.currentThread().getName();

        try {
            logger.debug("[{}] Rolling back transaction", thread);
            doRollbackTransaction();
            status.set(Status.ROLLEDBACK);

            doEndTransaction();
            context.remove();
            status.remove();

            return;
        }
        catch (SystemException rollbackEx) {
            logger.debug("[{}] Could not rollback transaction", thread, rollbackEx);
            status.set(Status.UNKNOWN);

            doEndTransaction();
            context.remove();
            status.remove();

            throw rollbackEx;
        }
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException {

        if (status.get() != Status.ACTIVE && status.get() != Status.MARKED_ROLLBACK) {
            throw new IllegalStateException("Transaction is not active");
        }

        String thread = Thread.currentThread().getName();

        logger.debug("[{}] Marking transaction as rollback only", thread);
        status.set(Status.MARKED_ROLLBACK);
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status getStatus() {
        return status.get();
    }

    protected ContextType getCurrentContext() {
        return context.get();
    }

    // -- Protected methods

    protected abstract ContextType createTransactionContext();

    protected abstract void doBeginTransaction(Transactional annotation) throws SystemException;

    protected abstract void doEndTransaction();

    protected abstract void doCommitTransaction() throws SystemException;

    protected abstract void doRollbackTransaction() throws SystemException;

}
