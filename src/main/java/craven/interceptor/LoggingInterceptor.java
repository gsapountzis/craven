package craven.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    public LoggingInterceptor() {
    }

    public Object log(InvocationContext ic) throws Exception {
        Object target = ic.getTarget();
        Method method = ic.getMethod();
        Object[] params = ic.getParameters();

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(target).append("] ");
        sb.append(method.getName()).append("(");

        if (params != null && params.length > 0) {
            sb.append("<").append(params[0]).append(">");
            for (int i = 1; i < params.length; i++) {
                sb.append(", ");
                sb.append("<").append(params[i]).append(">");
            }
        }

        sb.append(")");

        Object result = ic.proceed();

        if (result != null) {
            sb.append(" → ").append("<").append(result).append(">");
        }

        logger.debug(sb.toString());

        return result;
    }

}
