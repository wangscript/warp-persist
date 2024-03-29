package com.wideplay.codemonkey.web;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.persist.UnitOfWork;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.wideplay.codemonkey.model.SourceArtifact;
import com.wideplay.warp.Warp;
import com.wideplay.warp.WarpModule;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Created with IntelliJ IDEA.
 * On: 29/04/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class CodeMonkeyModule implements WarpModule {

    public void configure(Warp warp) {
        warp.install(PersistenceService.usingHibernate()

                .across(UnitOfWork.REQUEST)
                .transactedWith(TransactionStrategy.LOCAL)
                .forAll(Matchers.any())

                .buildModule()
        );
        
        warp.install(new AbstractModule() {

            protected void configure() {
                bind(Configuration.class).toInstance(new AnnotationConfiguration()
                    .addAnnotatedClass(SourceArtifact.class)
                    .setProperties(Initializer.loadProperties("persistence.properties")));
                
                bind(Initializer.class).asEagerSingleton();
            }
        });
    }
}
