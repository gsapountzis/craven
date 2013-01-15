package craven.interceptor;


import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import craven.transaction.Status;
import craven.transaction.TransactionManager;

public class TransactionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

    private final TransactionManager tm;

    public TransactionInterceptor(TransactionManager tm) {
        this.tm = tm;
    }

    public Object around(final InvocationContext ic) throws Exception {
        Object target = ic.getTarget();
        Method method = ic.getMethod();

        Transactional annotation = getAnnotation(Transactional.class, target, method);
        if (annotation == null) {
            throw new IllegalStateException("Could not find @Transactional annotation");
        }

        return _apply(annotation, new Callable<Object>() {
            @Override public Object call() throws Exception {
                return ic.proceed();
            }
        });
    }

    public <T> T apply(Callable<T> function) {
        return apply(TxConfig.DEFAULT, function);
    }

    public <T> T apply(Transactional annotation, Callable<T> function) {
        try {
            return _apply(annotation, function);
        }
        catch (Exception e) {
            TransactionInterceptor.<RuntimeException>rethrow(e);
            return null;
        }
    }

    private <T> T _apply(Transactional annotation, Callable<T> function) throws Exception {

        // TODO implement transaction propagation using an enum strategy

        try {
            logger.debug("Beginning transaction");
            tm.begin(annotation);

            T result = function.call();

            // The transaction manager guarantees than when commit or rollback complete, the thread is no longer
            // associated with a transaction.
            if (tm.getStatus() == Status.MARKED_ROLLBACK) {
                logger.debug("Rolling back transaction (marked)");
                tm.rollback();
            }
            else {
                logger.debug("Committing transaction");
                tm.commit();
            }

            return result;
        }
        catch (Exception ex) {
            if (tm.getStatus() != Status.NO_TRANSACTION) {
                // An active transaction means this was an application exception.
                try {
                    logger.debug("Rolling back transaction (exception)", ex);
                    tm.rollback();
                }
                catch (Exception rbEx) {
                    logger.debug("Could not rollback transaction", rbEx);
                }
            }

            throw ex;
        }
    }

    private static <A extends Annotation> A getAnnotation(Class<A> annotationType, Object target, Method method) {
        A annotation = getAnnotation(annotationType, method);
        if (annotation != null) {
            return annotation;
        }

        annotation = getAnnotation(annotationType, target.getClass());
        if (annotation != null) {
            return annotation;
        }

        return null;
    }

    private static <A extends Annotation> A getAnnotation(Class<A> annotationType, AnnotatedElement element) {
        if (element.isAnnotationPresent(annotationType)) {
            return element.getAnnotation(annotationType);
        }
        else {
            return null;
        }
    }

    // http://blog.jooq.org/2012/09/14/throw-checked-exceptions-like-runtime-exceptions-in-java/

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void rethrow(Exception e) throws E {
        throw (E) e;
    }

}
