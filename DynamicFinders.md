# Introduction #

A **Dynamic Finder** is a data retrieval **method** on a **concrete class**, annotated with the `@Finder` annotation. Warp Persist will intercept calls to these methods and generate the implementation.

```
public class PersonRepository {
    @Finder(query="from Person")
    public List<Person> allPeople() {
        // If configured correctly, this never executes.
        throw new AssertionError();
    }
} 
```

A **Dynamic Accessor** is an **interface** or **abstract class** holding Dynamic Finders, which can be specified when configuring a PersistenceService. These need to be specified because Guice does not intercept interfaces or abstract classes. Dynamic Finders on Dynamic Accessors also support `@Transactional`.

```
public interface PersonAccess {
    @Finder(query="from Person")
    public List<Person> allPeople();
} 
```

Note that the Dynamic Finders feature is currently **not** supported for DB4O.