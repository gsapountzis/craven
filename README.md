
# Programming experiment

A Java Interceptor is a higher-order function that applies to a set of named optional parameters (Annotation) and
a required function parameter (Function / Callable).

## Example

    final DataSource loggingDataSource = LoggingDataSourceFactory.create(dataSource);
    final JdbcTransactionManager transactionManager = new JdbcTransactionManager(loggingDataSource);
    final DataSource transactionalDataSource = new TransactionalDataSource(transactionManager);
    final AccountRepository accountRepository = new AccountRepository(transactionalDataSource);

    Long sapId = new TransactionInterceptor(transactionManager).apply(TxConfig.propagation(REQUIRES_NEW).build(), new Callable<Long>() {
        @Override public Long call() {
            Account sap = new Account();
            sap.setUsername("sap");
            sap.setPassword("pas");
            sap.setEmail("gsapountzis@gmail.com");

            return accountRepository.create(sap);
        }
    });

## Credits

Copies extensively from [Java EE](http://docs.oracle.com/javaee/) and [Spring](http://www.springsource.org/).

