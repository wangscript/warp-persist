Warp-persist is a lightweight library that provides persistence and transactions for applications using [Google Guice](http://code.google.com/p/google-guice).

You can use warp-persist inside web and servlet applications as well as EJB and simple desktop applications. All with a few simple configuration steps.

## Quick Hits ##

  * Inject DAOs & Repositories
  * Flexible units-of-work
  * Declarative transaction management
    * Using `@Transactional`
    * Or your own custom AOP
  * Dynamic Finders (reduce boilerplate query code)

Warp-persist fully supports:
  * [Hibernate](http://hibernate.org)
  * [Java Persistence API](http://java.sun.com/javaee/technologies/persistence.jsp) (JPA)
  * [db4objects](http://db4objects.com)

...and is easily extensible to any persistence system of your choice!