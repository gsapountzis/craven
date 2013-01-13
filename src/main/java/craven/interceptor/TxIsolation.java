package craven.interceptor;

import java.sql.Connection;

public enum TxIsolation {

    DEFAULT(-1),

    NONE(Connection.TRANSACTION_NONE),

    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    // --

    private final int level;

    public final int level() {
        return level;
    }

    private TxIsolation(int level) {
        this.level = level;
    }

    public static TxIsolation fromLevel(int level) {
        for (TxIsolation e : TxIsolation.values()) {
            if (e.level == level) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
