package craven.transaction.jdbc;


import java.sql.Connection;

import craven.interceptor.TxIsolation;
import craven.transaction.TransactionContext;

public class JdbcTransactionContext implements TransactionContext {

    // -- Attributes

    private Connection connection;

    // saved values of changed transaction attibutes - a null value means no change

    private Boolean previousAutoCommit;

    private TxIsolation previousIsolation;

    private Boolean previousReadOnly;

    // -- Constructors

    public JdbcTransactionContext() {
    }

    // -- Getters and Setters

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Boolean getPreviousAutoCommit() {
        return previousAutoCommit;
    }

    public void setPreviousAutoCommit(Boolean previousAutoCommit) {
        this.previousAutoCommit = previousAutoCommit;
    }

    public TxIsolation getPreviousIsolation() {
        return previousIsolation;
    }

    public void setPreviousIsolation(TxIsolation previousIsolation) {
        this.previousIsolation = previousIsolation;
    }

    public Boolean getPreviousReadOnly() {
        return previousReadOnly;
    }

    public void setPreviousReadOnly(Boolean previousReadOnly) {
        this.previousReadOnly = previousReadOnly;
    }

}
