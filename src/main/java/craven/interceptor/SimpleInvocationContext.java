package craven.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

public class SimpleInvocationContext implements InvocationContext {

    private final Object target;
    private final Method method;
    private Object[] parameters;

    private final Map<String, Object> contextData = new HashMap<String, Object>();

    public SimpleInvocationContext(Object target, Method method, Object[] parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParameters() throws IllegalStateException {
        return parameters;
    }

    @Override
    public void setParameters(Object[] params) throws IllegalStateException, IllegalArgumentException {
        int newParametersCount = (params == null) ? 0 : params.length;
        if (method.getParameterTypes().length != newParametersCount) {
            throw new IllegalArgumentException();
        }
        this.parameters = params;
    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object getTimer() {
        return null;
    }

    @Override
    public Object proceed() throws Exception {
        return method.invoke(target, parameters);
    }

}
