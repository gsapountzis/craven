package craven.transaction.jdbc;


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import craven.datasource.DelegatingConnection;
import craven.datasource.DelegatingDataSource;
import craven.transaction.Status;

public class TransactionalDataSource extends DelegatingDataSource {

    private final JdbcTransactionManager transactionManager;

    public TransactionalDataSource(JdbcTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    protected DataSource delegate() {
        return transactionManager.getDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(new ConnectionProvider() {
            @Override public Connection get() throws SQLException {
                return delegate().getConnection();
            }
        });
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection(new ConnectionProvider() {
            @Override public Connection get() throws SQLException {
                return delegate().getConnection(username, password);
            }
        });
    }

    private Connection getConnection(ConnectionProvider connectionProvider) throws SQLException {
        if (transactionManager.getStatus() != Status.NO_TRANSACTION) {
            return new TransactionalConnection(transactionManager.getTransactionalConnection());
        }
        else {
            return connectionProvider.get();
        }
    }

    private static interface ConnectionProvider {
        Connection get() throws SQLException;
    }

    private static class TransactionalConnection extends DelegatingConnection {

        private final Connection connection;

        private boolean closed = false;

        public TransactionalConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        protected Connection delegate() {
            return connection;
        }

        @Override
        protected void checkOpen() throws SQLException {
            if (closed) {
                throw new SQLException("Connection is closed.");
            }
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

    }

}
