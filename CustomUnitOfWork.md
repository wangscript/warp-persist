# Arbitrary Units of Work #

Sometimes you need to define a custom unit-of-work that doesn't fit into either http-requests or warp-persist-driven transactions. For example, you may want to do some background work in a non-request thread, or some initialization work in a startup thread (before any requests arrive). And which spans multiple transactions.

![http://warp-persist.googlecode.com/svn/wiki/custom_unitofwork.png](http://warp-persist.googlecode.com/svn/wiki/custom_unitofwork.png)

Or perhaps you are making a desktop app and have some other idea of a unit of work which doesn't quite fit into the Transaction and HTTP Request strategies we've just seen. Units of work directly correspond to the life of a single Hibernate `Session`, JPA `EntityManager` and db4o `ObjectContainer` respectively. Typically a unit of work is confined to one thread. This is also the best practice.

To start and end a unit of work arbitrarily, you can use the [WorkManager](WorkManager.md) interface provided by warp-persist.

## Rules of Thumb ##

When deciding if the arbitrary unit of work is right for you, there are some rules of thumb that might help:

  * Using `WorkManager` with the HTTP Request unit of work strategy _inside_ a request is not recommended
  * Using `WorkManager` with Transaction unit of work strategy is not terribly clever either
  * Using `WorkManager` with HTTP Request unit of work strategy, but _outside_ a request (i.e. in a background or init thread) is probably a good use case

Read more about using [WorkManager here](WorkManager.md).