
# Craven experiment

An Interceptor is a higher-order function that applies to a set of named optional parameters (Annotation) and
a required function parameter (Callable).

Can we use the same interceptor both programmatically and declaratively ? What does it take ?

## Declarative Example

This is what your are probably familiar with:

```java
public class DataSourceProducer {
    private final JdbcTransactionManager transactionManager;
    private final DataSource dataSource;

    public DataSourceProducer() {
        DataSource mainDataSource = null; // create or lookup DataSource ...
        this.transactionManager = new JdbcTransactionManager(mainDataSource);;
        this.dataSource = new TransactionalDataSource(transactionManager);
    }

    @Produces @ApplicationScoped
    public TransactionManager produceTransactionManager() {
        return transactionManager;
    }

    @Produces @ApplicationScoped
    public DataSource produceDataSource() {
        return dataSource;
    }
}
```

```java
public class AccountRepository {

    @Inject
    public AccountRepository(DataSource dataSource) {
        // ...
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

```java
    final DataSource loggingDataSource = LoggingDataSourceFactory.create(dataSource);
    final JdbcTransactionManager transactionManager = new JdbcTransactionManager(loggingDataSource);
    final DataSource transactionalDataSource = new TransactionalDataSource(transactionManager);

    final AccountRepository accountRepository = new AccountRepository(transactionalDataSource);

    Transactional annotation = TxConfig.propagation(REQUIRES_NEW).build();
    Long sapId = new TransactionInterceptor(transactionManager).apply(annotation, new Callable<Long>() {
        @Override public Long call() {
            Account sap = new Account();
            sap.setUsername("sap");
            sap.setPassword("pas");
            sap.setEmail("gsapountzis@gmail.com");

            return accountRepository.create(sap);
        }
    });
```

## Credits

Copies extensively from [Java EE](http://docs.oracle.com/javaee/) and [Spring](http://www.springsource.org/).

