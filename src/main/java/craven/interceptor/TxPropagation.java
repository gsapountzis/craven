package craven.interceptor;

public enum TxPropagation {

    REQUIRES_NEW,   // suspend (and start new)

    MANDATORY,      // throw exception (if not exists)

    REQUIRED,       // use existing (otherwise start new)

    SUPPORTS,       // use existing (otherwise do not start new)

    NEVER,          // throw exception (if exists)

    NOT_SUPPORTED   // suspend (and do not start new)

}
