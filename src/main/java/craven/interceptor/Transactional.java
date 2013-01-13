package craven.interceptor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Inherited
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Transactional {

    @Nonbinding TxPropagation propagation() default TxPropagation.REQUIRES_NEW;

    @Nonbinding TxIsolation isolation() default TxIsolation.DEFAULT;

    @Nonbinding int timeout() default -1;

    @Nonbinding boolean readOnly() default false;

}
