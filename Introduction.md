## Introduction ##

Most serious applications need to interact with a data store. This may be as simple as a desktop chat program that stores user preferences, or as complex as an enterprise web application dealing with several thousands of transactions to a relational database.

Warp-persist is a lightweight library that provides the bridge between your application code and the frameworks (Hibernate, JPA, etc.) that provide data persistence. It does for persistence, what [Google Guice](http://code.google.com/p/google-guice) does for your objects and services.

Mostly, it makes working with the data layer easy. And safe.

### Contributors ###

Warp-persist owes its simplicity and power in large part to valuable contributions from:

  * Jeffrey Chung (db4o support)
  * Robbie Vanbrabant (multimodules and warp-persist 2.0 extensibility features)

Warp-persist was created, and is maintained by [Dhanji R. Prasanna](http://twitter.com/dhanji).