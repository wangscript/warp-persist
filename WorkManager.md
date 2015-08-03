# The `WorkManager` interface #

`WorkManager` is a simple interface that lets you place yardsticks around a custom unit of work. It looks as follows:

```
public interface WorkManager {
    void beginWork();

    void endWork();
}
```

These two methods are pretty self-explanatory. Let's look at their semantics a bit closer.

## Using `WorkManager` ##

To use it, simply inject the `WorkManager` artifact into one of your classes:

```
public class MyBackgroundWorker {
    @Inject private WorkManager unitOfWork;
 
    public void doSomeWork() {
        unitOfWork.beginWork();

        try {       
 
            //do transactions, queries, whatever..
            //...
 
        } finally {
            unitOfWork.endWork(); 
        }
    } 
}
```

You are free to call any `@Transactional` methods while a unit of work is in progress this way. Multiple threads may go through `doSomeWork()` at the same time, provided they do not share any state. That is, you should not be reading or writing to objects visible across threads inside a single unit of work.


### Beginning Work ###

When you call `beginWork()`, warp-persist opens a session to the data layer underneath. This takes the form of a Hibernate `Session`, JPA `EntityManager` or db4o `ObjectContainer`, respectively. If one is already underway then `WorkManager` silently returns without opening a new session.

This behavior allows you to safely nest units of work within one another should you need a more granular body of work.

Consider the example of a message processor that periodically checks if there are undelivered messages in a table and if there are, marks them as failed so they are not re-tried. This can happen in a simple timer loop using `WorkManager`:

```
public class MessageCleanup {
    @Inject private WorkManager unitOfWork;
    ...

    //called periodically by timer
    public void cleanup() {
        unitOfWork.beginWork();

        try {       

            //read and cleanup undelivered messages
            //...

        } finally {
            unitOfWork.endWork(); 
        }
    }
}
```

Now, this works fine in the periodic operational case. However, when starting up the application if you want to perform the cleanup you don't want a second unit of work being opened unnecessarily. The cleanup operation at start time is logically part of the _start-time_ unit of work. By starting a unit of work in the start thread and then calling `MessageCleanup.cleanup()` from it, we are able to merge the work into the start-time unit:

```
public class Startup {
    @Inject private WorkManager unitOfWork;
    @Inject private MessageCleanup cleaner;
    ...

    //called once at start time
    public void init() {
        unitOfWork.beginWork();

        try {       
 
            //do some initialization work here
            //...

            cleaner.cleanup();
 
        } finally {
            unitOfWork.endWork(); 
        }
    }
}
```

Now you are able to use `MessageCleanup` both at start time (in a coarse-grained unit of work) and at runtime (in a more fine-grained timer call) without any change to the architecture.

### Ending Work ###

Method `endWork()` behaves analogously to `beginWork()`. When `endWork()` is called, any existing `Session/EntityManager/ObjectContainer` is closed and discarded in the current thread. It is safe to call `endWork()` multiple times around the same unit of work. It will return silently if no unit of work is currently active. In the preceeding example, `cleanup()` ended our unit of work, and calling `endWork()` subsequently simply returned.

## Thread Safety ##

`WorkManager` is thread-safe and can be cached and shared between threads or used in the same thread multiple times. `WorkManager` is also safe for _concurrent_ use, meaning multiple threads may share the same instance to manage their units of work without incurring any performance penalties.