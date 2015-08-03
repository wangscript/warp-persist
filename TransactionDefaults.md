# Transaction Defaults #

## AOP and Multiple Modules ##
Because Warp Persist's declarative transactions make use of Guice's built-in AOP, it also has a default set of "matchers" to which our transaction interceptor will be bound. The default configuration is as follows:
  * Any class
  * All methods with `@Transactional(unit=Defaults.DefaultUnit.class)`

Now, you might ask why that `Defaults.DefaultUnit.class` is there. The short answer is that it's usually the same as `@Transactional`, because that unit is defined as the default in the `@Transactional` annotation's definition. So why do we explicitly match on it? Well, if you really want to know... ;)

This, and the concept of a Unit Annotation in general, has to do with the Multiple Modules support in Warp Persist 2.0.

Because it is legal not to specify a unit annotation and go with the defaults, that "unitless" configuration can then be combined with a configuration that does have a unit, like this:
```
@Transactional(unit=MyUnit.class)
```

If you then, at the later time decide to remove the configuration with the unit annotation and merge it with the unitless configuration, what happens? Well, if the defaults matched on @Transactional without the unit, everything would magically work. BUT then your code will have confusing unit annotations in them, and that's not what you want. To protect people from making mistakes, we therefore also match on the unit annotation and not just the annotation itself.

The other, slightly more obvious reason is that you would also **have** to configure transactions manually if you mixed regular modules with modules that have a unit annotation, and we didn't match on the unit annotation by default. Otherwise the module without the unit annotation would also intercept the configurations that do have one. Feeling confused? Head over to the Multiple Modules section!

To reuse our default configuration for your custom transaction configuration needs, see the `PersistenceMatchers` and `Defaults` classes.

## Propagation ##
Warp Persist does not allow you to set a propagation setting. Basically Warp Persist always assumes "Requires" and will join with already existing transactions.

## Isolation ##
Warp Persist works with your JDBC settings. For Hibernate, you'd set the `hibernate.connection.isolation property`, use automatic versioning, custom `LockMode`s and so on in order to guarantee you data's consistency.

## Read-only Transactions ##
Our Hibernate transaction interceptor is the only interceptor that supports the `TransactionType.READ_ONLY` setting (by disabling the flushing of the `Session`). The default is `TransactionType.READ_WRITE`, and changing this setting has no effect for persistence engines other than Hibernate.

Example setting:
```
@Transactional(type=TransactionType.READ_ONLY)
public List<Product> findProducts() { ... }
```

## Timeout ##
We currently do not support setting a timeout on a transaction.