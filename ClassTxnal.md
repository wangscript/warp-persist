# Class-level @Transactional #

You can also annotate classes with @Transactional. This is useful because it saves you from repetitively tagging every method in a class. If you do decide to use this, you must change your warp-persist module configuration to look for @Transactional classes:

```
  Injector injector = Guice.createInjector(PersistenceService.usingHibernate()
		.across(UnitOfWork.REQUEST)
		.forAll(Matchers.annotatedWith(Transactional.class), Matchers.any())
		.buildModule(); 
```

This tells warp-persist to intercept any methods on classes marked with the @Transactional annotation. Now, your classes are less cluttered:

```
@Transactional
public class MyRepository {
    public void save(Thing t) { .. }
  
    public void remove(Thing t) { .. }   
 
    public Thing fetch(Long id) { .. }
}
```

Class-level `@Transactional` annotations support the same range of options that the method-level ones do. So you can specify `rollbackOn` and `exceptOn` clauses (see [Handling Exceptions](RollingBack.md)). And they apply to all methods in the class:

```
@Transactional(rollbackOn = IOException.class) //applies to all methods
public class MyRepository { 
    public void save(Thing t) throws IOException { .. }
  
    public void remove(Thing t) throws IOException { .. }   
 
    public Thing fetch(Long id) throws IOException { .. }
}
```


Most of the time, you can get away with a single `@Transactional` at the class level. But sometimes you need to customize just one method to behave differently. In these cases it's still redundant to mark all other methods with identical `@Transactional` annotations.

Warp-persist provides a facility where you can override the class's transactional behavior with a specific `@Transactional` on a particular method if desired:

```
@Transactional
public class MyRepository {
    public void save(Thing t) { .. }
  
    @Transactional(rollbackOn = NoSuchEntityException.class) //optional
    public void remove(Thing t) { .. }   
 
    public Thing fetch(Long id) { .. }
}
```

In the example above, `save()` and `fetch()` have standard transactional behavior as specified at the class-level. But `remove()` has a specific `rollbackOn` clause which is used instead.

Remember that `private` methods cannot be intercepted for transaction wrapping. This is because you cannot override private methods in subclasses. If any such methods are encountered, they will be silently ignored.