## Configuration ##

Warp-persist uses the per-transaction unit of work strategy by default. This means that every time a method marked with `@Transactional` is entered, a new session to the database is opened. And when this method returns, that session is closed. If we were using Hibernate, configuration would look as follows:

```
Guice.createInjector(..., PersistenceService.usingHibernate()
                         .across(UnitOfWork.TRANSACTION)
                         .buildModule());
```

This is exactly equivalent to the default setting:

```
Guice.createInjector(..., PersistenceService.usingHibernate()
                         .buildModule());
```