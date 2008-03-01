package com.wideplay.warp.persist;

import com.wideplay.warp.persist.dao.Finder;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * <p>
 * This is the core warp-persist artifact. It providers factories for generating guice
 * modules for your persistence configuration. It also must be injected into your code later
 * as a service abstraction for starting the underlying persistence engine (Hibernate or JPA).
 * </p>
 *
 * @author Dhanji R. Prasanna <a href="mailto:dhanji@gmail.com">email</a>
 * @since 1.0
 */
public abstract class PersistenceService {

    /**
     * Starts the underlying persistence engine and makes warp-persist ready for use.
     * For instance, with hibernate, it creates a SessionFactory and may open connection pools.
     * This method *must* be called by your code prior to using any warp-persist or hibernate artifacts.
     */
    public abstract void start();

    /**
     * A factory for warp-persist using Hibernate in your Guice module. See http://www.wideplay.com
     * for proper documentation on the EDSL.
     *
     * @return Returns the next step in the configuration chain.
     */
    public static SessionStrategyBuilder usingHibernate() {
        return new PersistenceServiceBuilderImpl(new PersistenceModule(PersistenceModule.PersistenceFlavor.HIBERNATE));
    }


    /**
     * A factory for warp-persist using JPA in your Guice module. See http://www.wideplay.com
     * for proper documentation on the EDSL. Any compliant implementation of JPA is supported (in theory).
     * Currently, TopLink, Hibernate and OpenJPA have shown positive results.
     *
     * @return Returns the next step in the configuration chain.
     */
    public static SessionStrategyBuilder usingJpa() {
        return new PersistenceServiceBuilderImpl(new PersistenceModule(PersistenceModule.PersistenceFlavor.JPA));
    }

    /**
     * A utility for testing if a given method is a dynamic finder.
     *
     * @param method A method you suspect is a Dynamic Finder.
     * @return Returns true if the method is annotated {@code @Finder}
     */
    public static boolean isDynamicFinder(Method method) {
        return method.isAnnotationPresent(Finder.class);
    }


    /**
     * A factory for warp-persist using Db4o in your Guice module. See http://www.wideplay.com
     * for proper documentation on the EDSL. Note that Db4o has slightly different semantics
     * than ORM frameworks like Hibernate and JPA. Consult the documentation carefully.
     *
     * @return Returns the next step in the configuration chain.
     */
    public static SessionStrategyBuilder usingDb4o() {
        return new PersistenceServiceBuilderImpl(new PersistenceModule(PersistenceModule.PersistenceFlavor.DB4O));
    }
}
