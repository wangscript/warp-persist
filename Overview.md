# Overview #

Persistence frameworks can be cumbersome to configure and manage. There are many aspects to manage in your code including everything from concurrency to data transactions. Warp-persist removes most of these problems by bridging the gap between persistence and dependency injection.

We currently support three popular persistence systems out of the box:

  * Hibernate
  * Java Persistence API (JPA)
  * Db4objects (a lightweight object database)

Java Persistence API (JPA) is supported for any compliant vendor implementation including TopLink, Kodo and Hibernate.

Warp-persist also provides the ability to extend and plug in other persistence systems with a few simple configuration changes.

## Architecture ##

Warp-persist integrates Hibernate, JPA and db4o with your Guice applications.

![http://warp-persist.googlecode.com/svn/wiki/warp-persist-arch1.png](http://warp-persist.googlecode.com/svn/wiki/warp-persist-arch1.png)