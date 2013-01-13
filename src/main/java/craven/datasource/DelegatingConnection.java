package craven.datasource;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public abstract class DelegatingConnection implements Connection {

    protected abstract Connection delegate();

    protected abstract void checkOpen() throws SQLException;

    // --

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate().isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate().unwrap(iface);
    }

    // --

    @Override
    public Statement createStatement() throws SQLException {
        checkOpen();
        return delegate().createStatement();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return delegate().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkOpen();
        return delegate().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    // --

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    // --

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkOpen();
        return delegate().prepareStatement(sql, columnNames);
    }

    // --

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkOpen();
        return delegate().prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkOpen();
        return delegate().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkOpen();
        return delegate().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    // --

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkOpen();
        return delegate().nativeSQL(sql);
    }

    // --

    @Override
    public Blob createBlob() throws SQLException {
        checkOpen();
        return delegate().createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        checkOpen();
        return delegate().createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        checkOpen();
        return delegate().createNClob();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        checkOpen();
        return delegate().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        checkOpen();
        return delegate().createStruct(typeName, attributes);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        checkOpen();
        return delegate().createSQLXML();
    }

    // --

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen();
        return delegate().getMetaData();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        checkOpen();
        return delegate().getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            checkOpen();
        }
        catch (SQLException e) {
            throw new SQLClientInfoException(null, e);
        }
        delegate().setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        checkOpen();
        return delegate().getClientInfo(name);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            checkOpen();
        }
        catch (SQLException e) {
            throw new SQLClientInfoException(null, e);
        }
        delegate().setClientInfo(name, value);
    }

    @Override
    public String getCatalog() throws SQLException {
        checkOpen();
        return delegate().getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkOpen();
        delegate().setCatalog(catalog);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkOpen();
        return delegate().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkOpen();
        delegate().setTypeMap(map);
    }

    // --

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkOpen();
        delegate().setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkOpen();
        return delegate().getAutoCommit();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkOpen();
        delegate().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkOpen();
        return delegate().getTransactionIsolation();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkOpen();
        delegate().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkOpen();
        return delegate().isReadOnly();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        checkOpen();
        delegate().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        checkOpen();
        return delegate().getHoldability();
    }

    // --

    @Override
    public void commit() throws SQLException {
        checkOpen();
        delegate().commit();
    }

    @Override
    public void rollback() throws SQLException {
        checkOpen();
        delegate().rollback();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkOpen();
        delegate().rollback(savepoint);
    }

    @Override
    public void close() throws SQLException {
        delegate().close();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return delegate().isValid(timeout);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate().isClosed();
    }

    // --

    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkOpen();
        return delegate().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkOpen();
        return delegate().setSavepoint(name);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkOpen();
        delegate().releaseSavepoint(savepoint);
    }

    // --

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return delegate().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkOpen();
        delegate().clearWarnings();
    }

}
