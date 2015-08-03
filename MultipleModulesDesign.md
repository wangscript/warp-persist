# Multiple Module Support Design #

## Motivation ##
Many larger projects need to be able to connect to multiple data sources. Warp Persist 1.0 does not support this.

## Approach ##
The current API is built around the assumption that only one data source exists, which is why we chose to keep that approach, and to repeat it for each required data source. So the user will have to install multiple persistence modules, one for each data source.

To differentiate between the different modules, we add the ability to specify a binding annotation when building the module. All persistence artifacts like a SessionFactory, the WorkManager, ... will be bound to that annotation. This will ensure that we do not create conflicting bindings.

At the same time, we implement another much requested feature: support for pluggable persistence engines. From an API perspective, this turns out to be a godsend for multimodules.

Here's an example of how it has currently been implemented:

```
HibernatePersistenceStrategy hibernate = HibernatePersistenceStrategy.builder()
                                                                     .annotatedWith(Sales.class).build();
// Connect to the Sales database using Hibernate.
PersistenceService.using(hibernate)
                  .across(UnitOfWork.TRANSACTION)
                  .buildModule();

```

Note that we choose builder() and not annotatedWith(...) to start with, because the latter would break the pluggable persistence strategies feature. If we enforce annotatedWith as the starting method, people will not be able to use their persistence strategy without an annotation.

Here's the same example, using 1.0 (without support for multiple modules):

```
// Connect to the Sales database using Hibernate.
PersistenceService.usingHibernate()
                  .across(UnitOfWork.TRANSACTION)
                  .buildModule();

```

## Internal Architecture Redesign ##
Focus area's:
  1. Self-containment for all persistence strategies (externally, but also internally).
  1. Get rid of as much static state as possible, for example the ...Holder classes.
  1. Continue to support @Transactional properly.
  1. Continue to support Dynamic Finders and Dynamic Accessors properly.
  1. SessionPerRequestFilter unification.
  1. Solid error detection and reporting.

Let's go over these one by one.

### Self-containment ###
Introduced several concepts:
  * PersistenceStrategy: defines a persistence strategy, like Hibernate, JPA or DB4O. This is the entry point to adding persistence strategies; it gives out a Guice Module that fully configures the strategy, based on ...
  * PersistenceConfiguration: all the configuration collected internally (through the public API). This is currently a concrete class, but it will be refactored to be an interface.
  * AbstractPersistenceModule: base Guice Module type that holds utility methods for the different persistence strategies.

All properties that are not exposed through the public API, included transaction strategies (which has been deprecated in the public API now) are expected to be included in the PersistenceStrategy's builder.

### Less static state ###
Currently used the Hibernate support as an example. The only static state will be in the global SessionPerRequestFilter, called SessionFilter. No more ...Holder types.
> -- Note: next to SessionFilter there will also be a LifecycleSessionFilter that stops/starts the PersistenceService(s).

A PersistenceStrategy will have a mechanism to "publish" its WorkManager and PersistenceService to Warp Persist, so it does not have to deal with static state itself. Currently these are simple getters on the PersistenceStrategy.

### Supporting @Transactional properly ###
When installing multiple modules, the user should restrict the types to intercept using the current forAll(...) API. This makes sure that Warp Persist will not open a transaction on the wrong persistence unit or open one when it's not needed.

This is not as convenient as it could be. We could use the binding annotation the user provides to restrict @Transactional automatically:
```
@Transactional(unit=Sales.class)
void store(Sale sale) { ... }
```

We will need to figure out how this will relate to the configuration specified with forAll, or even to the default configuration. Currently forAll overwrites the default configuration and assumes the user knows what he/she's doing.

### Supporting DF and DA properly ###
This part is tricky, because of two things:
  1. We proxy specified accessors using JDK proxies or CGLIB, and bind them without a binding annotation. This could cause conflicts when using multiple persistence modules.
  1. For concrete types, thus not using DA, we use Guice AOP, with a hard-coded Matcher configuration (all classes, all @Finder methods)

The first problem can be solved by binding these accessors to the same binding annotation the user specified. This is how it is currently implemented.
> -- (Side note: we could support @Transactional on DA's, something to think about.)
> -- **UPDATE:** See [Issue #9](https://code.google.com/p/warp-persist/issues/detail?id=#9)

The second problem does not have an obvious solution, mainly because it's hard to come up with a good API (like forAll) that the user can use to specify these matchers. Another approach would be to use the @Transactional idea above, which is what we currently implement:

```
@Finder(unit=Sales.class, ...)
void find(Long saleId) { ... }
```

We should enforce the use of this even for Dynamic Accessors to remain consistent. Also, we should consider making our custom matchers public.

### SessionPerRequestFilter ###
One SessionPerRequestFilter for all persistence strategies. See above.

### Solid error detection and reporting ###
  * Dynamic Accessors without `@Finder` should be detected. (use Guice's addError)
  * All providers or other artifacts (WorkManager, ...) we generate should output their binding unit annotation in their `toString()` (if there is one). This will improve the debugging experience.
  * TODO

### Misc. ###
All "magical" configuration values not provided through the API, for example a Hibernate Configuration that the user has bound in a Module, need to be specified in the PersistenceStrategy builder.

## Current Status ##
Mostly implemented. Some corner cases still need work, like specifying a unit on DA's.


---

Thanks for reading.

Robbie