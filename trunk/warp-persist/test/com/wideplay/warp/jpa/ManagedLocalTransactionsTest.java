package com.wideplay.warp.jpa;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.persist.Transactional;
import com.wideplay.warp.persist.UnitOfWork;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna <a href="mailto:dhanji@gmail.com">email</a>
 * @since 1.0
 */
@Test(suiteName = "jpa")
public class ManagedLocalTransactionsTest {
    private Injector injector;
    private static final String UNIQUE_TEXT = "some unique text" + new Date();
    private static final String TRANSIENT_UNIQUE_TEXT = "some other unique text" + new Date();


    @BeforeClass
    public void pre() {
        injector = Guice.createInjector(
                new AbstractModule() {

                    protected void configure() {
                        //tell Warp the name of the jpa persistence unit
                        bindConstant().annotatedWith(JpaUnit.class).to("testUnit");
                    }
                },
                PersistenceService.usingJpa()
            .across(UnitOfWork.TRANSACTION)
            .transactedWith(TransactionStrategy.LOCAL)
            .forAll(Matchers.any())
            .buildModule());

        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();
    }


    @AfterTest   //cleanup entitymanager in case some of the rollback tests left it in an open state
    public final void post() {
        EntityManagerFactoryHolder.closeCurrentEntityManager();
    }

    @AfterClass
    public final void postClass() {
        injector.getInstance(EntityManagerFactory.class).close();
    }

    @Test
    public void testSimpleTransaction() {
        injector.getInstance(TransactionalObject.class).runOperationInTxn();

        EntityManager em = injector.getInstance(EntityManager.class);
        assert !em.getTransaction().isActive() : "txn was not closed by transactional service";

        //test that the data has been stored
        Object result = em.createQuery("from JpaTestEntity where text = :text").setParameter("text", UNIQUE_TEXT).getSingleResult();
        EntityManagerFactoryHolder.closeCurrentEntityManager();

        assert result instanceof JpaTestEntity : "odd result returned fatal";

        assert UNIQUE_TEXT.equals(((JpaTestEntity)result).getText()) : "queried entity did not match--did automatic txn fail?";
    }

    @Test(expectedExceptions = NoResultException.class)
    public void testSimpleTransactionRollbackOnChecked() {
        try {
            injector.getInstance(TransactionalObject.class).runOperationInTxnThrowingChecked();
        } catch(IOException e) {
            //ignore
            System.out.println("caught (expecting rollback) " + e);

            EntityManagerFactoryHolder.closeCurrentEntityManager();
        }

        EntityManager em = EntityManagerFactoryHolder.getCurrentEntityManager();

        assert !em.getTransaction().isActive() : "EM was not closed by transactional service (rollback didnt happen?)";

        //test that the data has been stored
        Object result = em.createQuery("from JpaTestEntity where text = :text").setParameter("text", TRANSIENT_UNIQUE_TEXT).getSingleResult();
        EntityManagerFactoryHolder.closeCurrentEntityManager();

        assert null == result : "a result was returned! rollback sure didnt happen!!!";
    }

    @Test(expectedExceptions = NoResultException.class)
    public void testSimpleTransactionRollbackOnUnchecked() {
        try {
            injector.getInstance(TransactionalObject.class).runOperationInTxnThrowingUnchecked();
        } catch(RuntimeException re) {
            //ignore
            System.out.println("caught (expecting rollback) " + re);
            EntityManagerFactoryHolder.closeCurrentEntityManager();
        }

        EntityManager em = injector.getInstance(EntityManager.class);
        assert !em.getTransaction().isActive() : "Session was not closed by transactional service (rollback didnt happen?)";

        //test that the data has been stored
        Object result = em.createQuery("from JpaTestEntity where text = :text").setParameter("text", TRANSIENT_UNIQUE_TEXT).getSingleResult();
        EntityManagerFactoryHolder.closeCurrentEntityManager();
                                                  
        assert null == result : "a result was returned! rollback sure didnt happen!!!";
    }

    public static class TransactionalObject {
        private final EntityManager em;

        @Inject
        public TransactionalObject(EntityManager em) {
            this.em = em;
        }

        @Transactional
        public void runOperationInTxn() {
            JpaTestEntity entity = new JpaTestEntity();
            entity.setText(UNIQUE_TEXT);
            em.persist(entity);
        }

        @Transactional(rollbackOn = IOException.class)
        public void runOperationInTxnThrowingChecked() throws IOException {
            JpaTestEntity entity = new JpaTestEntity();
            entity.setText(TRANSIENT_UNIQUE_TEXT);
            em.persist(entity);

            throw new IOException();
        }

        @Transactional
        public void runOperationInTxnThrowingUnchecked() {
            JpaTestEntity entity = new JpaTestEntity();
            entity.setText(TRANSIENT_UNIQUE_TEXT);
            em.persist(entity);

            throw new IllegalStateException();
        }
    }
}