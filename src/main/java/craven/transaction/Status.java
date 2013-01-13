package craven.transaction;

public enum Status {

    // --

    ACTIVE,

    MARKED_ROLLBACK,

    // --

    PREPARED,

    COMMITTED,

    ROLLEDBACK,

    // --

    UNKNOWN,

    NO_TRANSACTION,

}
