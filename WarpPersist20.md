# Warp Persist 2.0 #

The second major Warp Persist release was based around the following themes:
  * Making things even simpler.
  * Supporting third party persistence extensions.
  * Support the usage of multiple (instances of) persistence engines next to each other.

## New Features ##
  * Pluggable persistence engines (`Persistence.using(...)`) and an SPI (`com.wideplay.warp.persist.spi`).
  * Support for multiple persistence modules (MultipleModulesDesign).
  * Hibernate, JPA and DB4O now expose a builder API as an alternative to automagical bindings (using the previous two features).
  * `SessionPerRequestFilter` unification: `PersistenceFilter`. It also starts / stops all `PersistenceService` instances.
  * `PersistenceService`s now start automatically (but can still be started on demand).
  * Hibernate Dynamic Finders now support `@Named` arrays and collections.
  * Dynamic Accessors now support `@Transactional`.
  * Calls to `forAll(...)` in the fluent interface can now be chained to bind the transaction interceptor multiple times.
  * We now ship a module with utility bindings called `PersistenceServiceExtrasModule`. It currently only contains a binding for `List<PersistenceService>`.
  * Guice 2.0 is now supported and recommended.
  * New documentation homepage: http://code.google.com/docreader/#p=warp-persist

## Breaking Changes ##
  * Removed all `SessionPerRequestFilter` classes.
  * Moved Hibernate, JPA and DB4O support into the `com.wideplay.warp.persist` package hierarchy.
  * Removed JTA legacy code (no longer supported)
  * Removed `TransactionStrategy` and `transactedWith(...)` in the fluent interface (only local transactions are supported)
  * Fluent interface returns different types in some cases (for `forAll` chaining)
  * We now throw more exceptions, for example when using `TransactionType.READ_ONLY` with something else than Hibernate, or when trying to use Dynamic Finders with DB4O in Stage.DEVELOPMENT.