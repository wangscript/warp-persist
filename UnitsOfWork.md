# Units of Work #

A unit of work is a logical session to the data layer in which you perform some series of related tasks. Units of work are typically more coarse-grained than transactions and have no notion of a _rollback_ or _commit_. In more practical terms, a unit of work directly corresponds to a Hibernate `Session`, a JPA `EntityManager` and a db4o `ObjectContainer`, respectively.

All interaction with the data layer _must_ occur inside a unit of work. Warp-persist provides multiple strategies for controlling the duration and context of a unit of work.  The simplest of these is Transaction unit of work.


## Transaction Unit of Work ##

As the name implies, Transaction unit of work strategy involves starting and ending a unit of work around a single persistence transaction. When a transaction begins (triggered by entry into an `@Transactional` method), a new unit of work is automatically started, and when the transaction ends the unit of work is immediately closed.

![http://warp-persist.googlecode.com/svn/wiki/txn_unitofwork.png](http://warp-persist.googlecode.com/svn/wiki/txn_unitofwork.png)

This strategy is useful when you are doing a logical group of tasks within one transaction. It is often employed in middle tier or desktop applications, where the concept of an HTTP request does not exist. Or where one is not immediately relevant. This is also similar to the way container-managed transactions work in EJB.

## HTTP Request Unit of Work (Open Session In View) ##

By far the most popular and useful strategy, this involves a unit of work being created and destroyed around each HTTP request. Inside it, you may have multiple transactions but they all operate through the same persistence session. This is particularly useful in web applications where _lazy fetching_ is important.

![http://warp-persist.googlecode.com/svn/wiki/request_unitofwork.png](http://warp-persist.googlecode.com/svn/wiki/request_unitofwork.png)

HTTP Request unit of work strategy is implemented in warp-persist via the use of a Servlet _Filter_. This filter runs before and after every incoming HTTP request and ensures a unit of work is present.

JPA and Hibernate sometimes refer to this as the _Open Session In View_ design pattern.

## Custom Unit of Work ##

It is sometimes necessary to create a unit of work specific to your own problem domain. This may not quite fit within HTTP request or transaction unit of work strategies. A good example is doing some data operations during an application's startup. Or performing some work periodically in a background thread.

![http://warp-persist.googlecode.com/svn/wiki/custom_unitofwork.png](http://warp-persist.googlecode.com/svn/wiki/custom_unitofwork.png)

Warp-persist allows you to define arbitrary units of work that nest many transactions and span whatever duration you require.