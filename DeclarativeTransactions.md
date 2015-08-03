# Method-level @Transactional #

By default, any method marked with @Transactional will have a transaction started before, and ended after it is called. The only restriction is that these methods must be on objects that were created by Guice. They may have any visibility except private.

```
@Transactional
public void myMethod() { ... }
```

The default configuration from warp-persist looks for @Transactional annotations on ALL classes that Guice manages. You can restrict this scan by specifying a class and method matcher as follows:

```
Injector injector = Guice.createInjector(PersistenceService.usingHibernate().across(UnitOfWork.REQUEST)
.forAll(Matchers.subclassesOf(MyClass.class), Matchers.any())
.buildModule(); 
```

Please refer to the Guice documentation on how to use matchers (look at the section entitled "intercepting methods").
Responding to exceptions

If such a transactional method encounters an unchecked exception (any kind of RuntimeException), the transaction will be rolled back. Checked exceptions are ignored (and the transaction will be committed anyway).

To change this behavior, you can specify your own exceptions (checked or unchecked) on a per-method basis:

```
@Transactional(rollbackOn = IOException.class)
public void myMethod() throws IOException { ... }
```

Once you specify a rollbackOn clause, only the given exceptions and their subclasses will be considered for rollback. Everything else will be committed. Note that you can specify any combination of exceptions using array literal syntax:

```
@Transactional(rollbackOn = { IOException.class, RuntimeException.class, ... })
```

It is sometimes necessary to have some general exception types you want to rollback but particular subtypes that are still allowed:

```
@Transactional(rollbackOn = IOException.class, exceptOn = FileNotFoundException.class)
```

In the above case, any IOException (and any of its subtypes) except FileNotFoundException will trigger a rollback. In the case of FileNotFoundException, a commit will be performed and the exception propagated to the caller anyway. Note that you can specify any combination of checked or unchecked exceptions.