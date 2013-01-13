package craven.transaction.jdbc;


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import craven.datasource.JdbcUtils;
import craven.interceptor.Transactional;
import craven.interceptor.TxIsolation;
import craven.transaction.Status;
import craven.transaction.SystemException;
import craven.transaction.TransactionManagerTemplate;

public class JdbcTransactionManager extends TransactionManagerTemplate<JdbcTransactionContext> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionManager.class);

    private final DataSource dataSource;

    public JdbcTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected JdbcTransactionContext createTransactionContext() {
        return new JdbcTransactionContext();
    }

    @Override
    protected void doBeginTransaction(Transactional annotation) throws SystemException {
        JdbcTransactionContext currentContext = getCurrentContext();

        try {
            Connection connection = dataSource.getConnection();
            currentContext.setConnection(connection);

            boolean currentAutoCommit = connection.getAutoCommit();

            setReadOnly(currentAutoCommit, annotation.readOnly());
            setIsolation(currentAutoCommit, annotation.isolation());

            if (currentAutoCommit) {
                currentContext.setPreviousAutoCommit(currentAutoCommit);
                connection.setAutoCommit(false);
            }
        }
        catch (SQLException e) {
            throw new SystemException(e);
        }
        catch (Throwable t) {
            throw new SystemException(t);
        }
    }

    @Override
    protected void doEndTransaction() {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        Boolean previousAutoCommit = currentContext.getPreviousAutoCommit();

        if (previousAutoCommit != null) {
            try {
                connection.setAutoCommit(true);
            }
            catch (SQLException e) {
                logger.debug("Could not restore session auto-commit mode", e);
            }
            catch (Throwable t) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                logger.debug("Unexpected exception while restoring auto-commit mode", t);
            }
        }

        try {
            unsetIsolation();
            unsetReadOnly();
        }
        catch (SQLException e) {
            logger.debug("Could not restore session characteristics", e);
        }
        catch (Throwable t) {
            // We don't trust the JDBC driver: It might throw RuntimeException or Error.
            logger.debug("Unexpected exception while restoring session characteristics", t);
        }

        JdbcUtils.closeConnection(connection);
    }

    // --

    private void setReadOnly(boolean autoCommit, boolean newReadOnly) throws SQLException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        boolean currentReadOnly = connection.isReadOnly();
        if (newReadOnly != currentReadOnly) {
            if (autoCommit) {
                currentContext.setPreviousReadOnly(currentReadOnly);
                // Note: This method cannot be called during a transaction.
                connection.setReadOnly(newReadOnly);
            }
            else {
                throw new SystemException("Cannot set transaction read-only, session already in transaction");
            }
        }
    }

    private void setIsolation(boolean autoCommit, TxIsolation newIsolation) throws SQLException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        if (newIsolation != TxIsolation.DEFAULT) {
            TxIsolation currentIsolation = TxIsolation.fromLevel(connection.getTransactionIsolation());
            if (newIsolation != currentIsolation) {
                if (autoCommit) {
                    currentContext.setPreviousIsolation(currentIsolation);
                    // Note: If this method is called during a transaction, the result is implementation-defined.
                    connection.setTransactionIsolation(newIsolation.level());
                }
                else {
                    throw new SystemException("Cannot set transaction isolation level, session already in transaction");
                }
            }
        }
    }

    private void unsetReadOnly() throws SQLException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        Boolean previousReadOnly = currentContext.getPreviousReadOnly();
        if (previousReadOnly != null) {
            connection.setReadOnly(previousReadOnly);
        }
    }

    private void unsetIsolation() throws SQLException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        TxIsolation previousIsolation = currentContext.getPreviousIsolation();
        if (previousIsolation != null) {
            connection.setTransactionIsolation(previousIsolation.level());
        }
    }

    // --

    @Override
    protected void doCommitTransaction() throws SystemException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        try {
            connection.commit();
        }
        catch (SQLException e) {
            throw new SystemException(e);
        }
        catch (Throwable t) {
            throw new SystemException(t);
        }
    }

    @Override
    protected void doRollbackTransaction() throws SystemException {
        JdbcTransactionContext currentContext = getCurrentContext();
        Connection connection = currentContext.getConnection();

        try {
            connection.rollback();
        }
        catch (SQLException e) {
            throw new SystemException(e);
        }
        catch (Throwable t) {
            throw new SystemException(t);
        }
    }

    // --

    DataSource getDataSource() {
        return dataSource;
    }

    Connection getTransactionalConnection() {
        if (getStatus() != Status.NO_TRANSACTION) {
            return getCurrentContext().getConnection();
        }
        else {
            return null;
        }
    }

}
