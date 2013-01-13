package craven.datasource;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.interceptor.InvocationContext;
import javax.sql.DataSource;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import craven.interceptor.LoggingInterceptor;
import craven.interceptor.SimpleInvocationContext;

public class LoggingDataSourceFactory {

    private static final boolean dataSourceLoggingEnabled = true;
    private static final boolean connectionLoggingEnabled = true;
    private static final boolean statementLoggingEnabled = false;

    private static final LoggingInterceptor interceptor = new LoggingInterceptor();

    private LoggingDataSourceFactory() {
    }

    public static DataSource create(DataSource ds) {
        return factoryResultProcessor.apply(ds);
    }

    private static Function<DataSource, DataSource> factoryResultProcessor = new Function<DataSource, DataSource>() {
        @SuppressWarnings("unused")
        @Override
        public DataSource apply(DataSource ds) {
            if (dataSourceLoggingEnabled || connectionLoggingEnabled || statementLoggingEnabled) {
                return createLoggingDataSource(ds);
            }
            else {
                return ds;
            }
        }
    };

    private static Function<Object, Object> dataSourceResultProcessor = new Function<Object, Object>() {
        @SuppressWarnings("unused")
        @Override
        public Object apply(Object result) {
            if (connectionLoggingEnabled || statementLoggingEnabled) {
                if (result instanceof Connection) {
                    Connection conn = (Connection) result;
                    return createLoggingConnection(conn);
                }
            }
            return result;
        }
    };

    private static Function<Object, Object> connectionResultProcessor = new Function<Object, Object>() {
        @Override
        public Object apply(Object result) {
            if (statementLoggingEnabled) {
                if (result instanceof Statement) {
                    Statement stmt = (Statement) result;
                    return createLoggingStatement(stmt);
                }
            }
            return result;
        }
    };

    private static DataSource createLoggingDataSource(DataSource ds) {

        return (DataSource) Proxy.newProxyInstance(
                                    DataSource.class.getClassLoader(),
                                    new Class[] { DataSource.class },
                                    new LoggingInvocationHandler(ds, dataSourceLoggingEnabled, dataSourceResultProcessor) );
    }

    private static Connection createLoggingConnection(Connection conn) {

        return (Connection) Proxy.newProxyInstance(
                                    Connection.class.getClassLoader(),
                                    new Class[] { Connection.class },
                                    new LoggingInvocationHandler(conn, connectionLoggingEnabled, connectionResultProcessor) );
    }

    private static Statement createLoggingStatement(Statement stmt) {
        Class<? extends Statement> iface = Statement.class;
        if (stmt instanceof PreparedStatement) {
            iface = PreparedStatement.class;
        }
        else if (stmt instanceof CallableStatement) {
            iface = CallableStatement.class;
        }

        return (Statement) Proxy.newProxyInstance(
                                    Statement.class.getClassLoader(),
                                    new Class[] { iface },
                                    new LoggingInvocationHandler(stmt, true, Functions.identity()) );
    }

    private static class LoggingInvocationHandler implements InvocationHandler {

        private final Object target;
        private final boolean loggingEnabled;
        private final Function<Object, Object> resultProcessor;

        public LoggingInvocationHandler(Object target, boolean loggingEnabled, Function<Object, Object> resultProcessor) {
            this.target = target;
            this.loggingEnabled = loggingEnabled;
            this.resultProcessor = resultProcessor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                InvocationContext ic = new SimpleInvocationContext(target, method, args);
                Object result = loggingEnabled ? interceptor.around(ic) : ic.proceed();
                return resultProcessor.apply(result);
            }
            catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }

    }

}
