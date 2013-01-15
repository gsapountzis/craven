package craven.transaction;

import craven.interceptor.Transactional;

public interface TransactionManager {

    /**
     * Create a new transaction and associate it with the current thread.
     */
    public void begin() throws IllegalStateException, SystemException;

    /**
     * Create a new transaction and associate it with the current thread.
     */
    public void begin(Transactional annotation) throws IllegalStateException, SystemException;

    /**
     * Commit the transaction associated with the current thread.
     *
     * When this method completes, the thread is no longer associated with a transaction.
     */
    public void commit() throws IllegalStateException, RollbackException, SystemException;

    /**
     * Roll back the transaction associated with the current thread.
     *
     * When this method completes, the thread is no longer associated with a transaction.
     */
    public void rollback() throws IllegalStateException, SystemException;

    /**
     * Modify the transaction associated with the current thread such that the only possible outcome of the transaction is to roll back the transaction.
     */
    public void setRollbackOnly() throws IllegalStateException;

    /**
     * Modify the timeout value that is associated with transactions started by the current thread with the begin method.
     *
     * If an application has not called this method, the transaction service uses some default value for the transaction timeout.
     */
    public void setTransactionTimeout(int seconds) throws SystemException;

    /**
     * Obtain the status of the transaction associated with the current thread.
     */
    public Status getStatus();

}
