package org.cfr.capsicum.datasource;

import java.util.List;

import junit.framework.Assert;

import org.cfr.capsicum.domain.user.User;
import org.cfr.capsicum.test.AbstractCayenneJUnit4SpringContextTests;
import org.cfr.capsicum.test.CayenneRuntimeContext;
import org.cfr.capsicum.user.IUserStore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ContextConfiguration(locations = { "classpath:org/cfr/capsicum/datasource/cayenne-transaction-manager-beans-definitions.xml" })
@CayenneRuntimeContext()
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class CayenneTransactionManagerTest extends AbstractCayenneJUnit4SpringContextTests {

    @Autowired
    private IUserStore userStore;

    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeTransaction
    @Override
    public void beforeTransaction() throws Exception {
        super.beforeTransaction();
        userStore.deleteAll();
    }

    @Test
    @Transactional()
    public void simpleCayenneInitialization() {
        List<User> list = userStore.findAllUser();
        Assert.assertEquals(0, list.size());
    }

    @Test
    @Transactional
    public void insertInTransaction() {
        boolean rollback = false;
        userStore.insertTestUsersInTransaction(rollback);
        List<User> list = userStore.findAllUser();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void insertNoTransaction() {
        boolean rollback = false;
        userStore.insertTestUsersInTransaction(rollback);
        List<User> list = userStore.findAllUser();
        Assert.assertEquals(3, list.size());
    }

    @Test(expected = RuntimeException.class)
    @Transactional(rollbackFor = RuntimeException.class)
    public void insertFailInTransaction() {
        boolean rollback = true;
        userStore.insertTestUsersInTransaction(rollback);
    }

    @Test()
    public void insertFailNoTransaction() {
        boolean rollback = true;
        try {
            userStore.insertTestUsersInTransaction(rollback);
            Assert.fail();
        } catch (RuntimeException ex) {

        }
        List<User> list = userStore.findAllUser();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void requiresNewTransaction() {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(
                TransactionDefinition.PROPAGATION_REQUIRED);
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, definition);
        final boolean raiseException = true;
        try {
            transactionTemplate.execute(new TransactionCallback<Void>() {

                @Override
                public Void doInTransaction(TransactionStatus status) {
                    userStore.insertTestUsersInTransaction(false);
                    userStore.insertTestUsersInRequiresNewTransaction(false);
                    if (raiseException)
                        throw new RuntimeException("That's append");
                    return null;
                }
            });
            Assert.fail();
        } catch (RuntimeException ex) {
            // good
        } catch (Throwable ex) {
            Assert.fail();
        }
        List<User> list = userStore.findAllUser();
        Assert.assertEquals(1, list.size());
    }
}
