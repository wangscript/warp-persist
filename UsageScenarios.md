## Usage in Desktop Environments ##

The simplest and most straightforward usage scenario is firing up warp-persist from a Java `main()` method. This can be done in a Swing, SWT or simple command-line app, as you choose. In such environments, the [transaction unit of work](TransactionUnitOfWork.md) is recommended.

## Usage in Web Environments ##

Warp-persist works naturally inside servlet containers and web application frameworks like Struts2, Apache Wicket and [warp-widgets](http://code.google.com/p/warp-core). (Note that warp-persist has no dependency on warp-widgets)

With a few simple configuration steps, any web application can take advantage of dependency injection, [declarative transactions](DeclarativeTransactions.md) and [query abstraction](DynamicFinders.md) using a variety of popular persistence systems.

Warp-persist also supports the _open-session-in-view_ paradigm natively, via the [HTTP Request unit of work](OSIVUnitOfWork.md).

## Usage in Enterprise Environments ##

Warp-persist also provides support for [arbitrary units of work](CustomUnitOfWork.md), which may group several related transactions together, outside HTTP requests or direct user interactions. This is particularly useful when processing data in background threads driven by timers or during startup of a large application.

Combining this with desktop or web profiles is also trivially simple.

## Why not roll my own? ##

Yea, so it looks easy. And it may even be easy to get off the ground. But there are several quite tricky design problems that we've solved carefully and painstakingly.

And covered with a battery of robust integration tests. Hibernate, JPA and db4o each play their own quirks which are hard to detect until things start to go wrong deep into the life of a project. You'd be well served taking advantage of our efforts.

Furthermore, warp-persist was designed with core Guice idioms firmly in mind:
  * type-safety
  * simplicity
  * testability
  * fluent configuration
  * focus (what you need, now. Not the kitchen sink)

For a quick sample, this is how easy it is to configure warp-persist with hibernate and the _open-session-in-view_ paradigm:

```
Guice.createInjector(..., PersistenceService
                           .usingHibernate()
                           .across(UnitOfWork.REQUEST)
                           .buildModule());
```

It looks easy because in large part, warp-persist _makes_ it look easy.

## Do you support 

<insert\_persistence\_library>

? ##

We have reliable tests around Hibernate, JPA (using Hibernate), and db4o. And several successful reports from users with Oracle TopLink and Apache OpenJPA.

However, there are many other libraries out there and coming with warp-persist 2.0, we provide an easy extension mechanism to configure and use your favorite persistence system.