package craven.interceptor;

import javax.enterprise.util.AnnotationLiteral;

public class TxConfig extends AnnotationLiteral<Transactional> implements Transactional {

    public static Transactional DEFAULT = new TxConfig();

    private final TxPropagation propagation;
    private final TxIsolation isolation;
    private final int timeout;
    private final boolean readOnly;

    @Override
    public TxPropagation propagation() {
        return propagation;
    }

    @Override
    public TxIsolation isolation() {
        return isolation;
    }

    @Override
    public int timeout() {
        return timeout;
    }

    @Override
    public boolean readOnly() {
        return readOnly;
    }

    public static Builder propagation(TxPropagation propagation) {
        return new Builder().propagation(propagation);
    }

    public static Builder isolation(TxIsolation isolation) {
        return new Builder().isolation(isolation);
    }

    public static Builder timeout(int timeout) {
        return new Builder().timeout(timeout);
    }

    public static Builder readOnly(boolean readOnly) {
        return new Builder().readOnly(readOnly);
    }

    private TxConfig() {
        Transactional defaultTx = DefaultTransactional.class.getAnnotation(Transactional.class);

        this.propagation = defaultTx.propagation();
        this.isolation = defaultTx.isolation();
        this.timeout = defaultTx.timeout();
        this.readOnly = defaultTx.readOnly();
    }

    private TxConfig(Builder builder) {
        this.propagation = builder.propagation;
        this.isolation = builder.isolation;
        this.timeout = builder.timeout;
        this.readOnly = builder.readOnly;
    }

    /**
     * Default {@link Transactional} attribute values.
     */
    @Transactional
    private static final class DefaultTransactional { }

    /**
     * Builder for {@link Transactional} to simulate named optional parameters.
     */
    public static class Builder {

        private TxPropagation propagation = DEFAULT.propagation();
        private TxIsolation isolation = DEFAULT.isolation();
        private int timeout = DEFAULT.timeout();
        private boolean readOnly = DEFAULT.readOnly();

        public Builder() {
        }

        public Builder propagation(TxPropagation propagation) {
            this.propagation = propagation;
            return this;
        }

        public Builder isolation(TxIsolation isolation) {
            this.isolation = isolation;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Transactional build() {
            return new TxConfig(this);
        }

    }

}
