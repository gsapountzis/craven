
# Craven experiment

An Interceptor is a higher-order function that applies to a set of named optional parameters (Annotation) and
a required function parameter (Callable).

Can we use the same interceptor both programmatically and declaratively ? What does it take ?

## Declarative Example

You are probably familiar with:

```java
public class DataSourceProducer {
    private final JdbcTransactionManager transactionManager;
    private final DataSource dataSource;

    public DataSourceProducer() {
        this.transactionManager = new JdbcTransactionManager(mainDataSource);
        this.dataSource = new TransactionalDataSource(transactionManager);
    }

    @Produces @Singleton
    public TransactionManager produceTransactionManager() {
        return transactionManager;
    }

    @Produces @Singleton
    public DataSource produceDataSource() {
        return dataSource;
    }
}
```

```java
public class AccountRepository {
    private final DataSource dataSource;

    @Inject
    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
```

```java
public class AccountResource {
    private final AccountRepository accountRepository;

    @Inject
    public AccountResource(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Long createAccount() {
        Account sap = new Account();
        sap.setUsername("sap");
        sap.setPassword("pas");
        sap.setEmail("gsapountzis@gmail.com");

        return accountRepository.create(sap);
    }
}
```

## Programmatic Example

What does it take to be able to use JavaEE interceptors programmatically ?

Either using a dependency injection framework:

```java
public class AccountResource {
    private final Provider<TransactionInterceptor> transactionInterceptor;
    private final AccountRepository accountRepository;

    @Inject
    public AccountResource(Provider<TransactionInterceptor> transactionInterceptor,
                           AccountRepository accountRepository) {
        this.transactionInterceptor = transactionInterceptor;
        this.accountRepository = accountRepository;
    }

    public Long createAccount() {
        Transactional annotation = TxConfig.propagation(REQUIRES_NEW).build();
        return transactionInterceptor.get().apply(annotation, new Callable<Long>() {
            @Override public Long call() {
                Account sap = new Account();
                sap.setUsername("sap");
                sap.setPassword("pas");
                sap.setEmail("gsapountzis@gmail.com");

                return accountRepository.create(sap);
            }
        });
    }
}
```

Or with manual dependency injection:

```java
public class Application {

    public void init() {
        DataSource loggingDataSource = LoggingDataSourceFactory.create(dataSource);
        JdbcTransactionManager transactionManager = new JdbcTransactionManager(loggingDataSource);
        DataSource transactionalDataSource = new TransactionalDataSource(transactionManager);

        Provider<TransactionInterceptor> transactionInterceptor = new Provider<TransactionInterceptor> {
            @Override public TransactionInterceptor get() {
                return new TransactionInterceptor(transactionManager);
            }
        };

        AccountRepository accountRepository = new AccountRepository(transactionalDataSource);
        AccountResource accountResource = new AccountResource(transactionInterceptor, accountRepository);
    }
}
```

```java
public class AccountResource {
    private final Provider<TransactionInterceptor> transactionInterceptor;
    private final AccountRepository accountRepository;

    public AccountResource(Provider<TransactionInterceptor> transactionInterceptor,
                           AccountRepository accountRepository) {
        this.transactionInterceptor = transactionInterceptor;
        this.accountRepository = accountRepository;
    }

    public Long createAccount() {
        Transactional annotation = TxConfig.propagation(REQUIRES_NEW).build();
        return transactionInterceptor.get().apply(annotation, new Callable<Long>() {
            @Override public Long call() {
                Account sap = new Account();
                sap.setUsername("sap");
                sap.setPassword("pas");
                sap.setEmail("gsapountzis@gmail.com");

                return accountRepository.create(sap);
            }
        });
    }
}
```

## Conclusion

Using interceptors programmatically requires little, namely:

* the addition of an `AnnotationLiteral` subclass that implements the relevant `Annotation` interface
  with its accompying `Builder` to simulate named optional parameters, check the `TxConfig` class.
* the refactoring of the relevant `Interceptor` class around the `<T> T apply(<A extends Annotation>, Callable<T>)` method,
  check the `TransactionInterceptor` class.

And we have reached a state where everybody is happy (or unhappy), both new developers who are customed to
working with a framework and older developers who have valid reasons for not using a framework in their codebase.

## Credits

Copies extensively from [Java EE](http://docs.oracle.com/javaee/) and [Spring](http://www.springsource.org/).

