package com.wideplay.warp.hibernate;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class SessionFactoryDuplicationAwareTest {
    private Injector injector;

    @BeforeTest
    public void pre() {
        injector = Guice.createInjector(PersistenceService.usingHibernate()
            .across(UnitOfWork.TRANSACTION)
            .transactedWith(TransactionStrategy.LOCAL)
            .forAll(Matchers.any())
            .buildModule(),
                new AbstractModule() {

                    protected void configure() {
                        bind(Configuration.class).toInstance(new AnnotationConfiguration()
                            .addAnnotatedClass(HibernateTestEntity.class)
                            .setProperties(Initializer.loadProperties("spt-persistence.properties")));
                    }
                });
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSessionFactoryDuplicateAvoidance() {
        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();

        //startup persistence again (should fail!)
        injector.getInstance(PersistenceService.class)
                .start();

        //obtain sessionfactory
        assert false: "Session factory duplication!!!";
    }
}
