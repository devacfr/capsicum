/**
 * Copyright 2014 devacfr<christophefriederich@mac.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfr.capsicum.spring.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationException;
import org.apache.cayenne.validation.ValidationResult;
import org.cfr.capsicum.spring.support.CayenneValidationException;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class CayenneTransactionManagerTest extends EasyMockTestCase {

    DataSource dataSource;

    Connection connection;

    ObjectContext objectContext;

    CayenneRuntime cayenneRuntime;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        objectContext = mock(ObjectContext.class);
        cayenneRuntime = mock(CayenneRuntime.class);
    }

    @Test()
    public void comitFailWithValidationException() throws SQLException {

        ValidationResult result = new ValidationResult();
        result.addFailure(BeanValidationFailure.validateMandatory(new Foo(), "name"));
        Exception throwable = new ValidationException(result);

        objectContext.commitChanges();
        expectLastCall().andThrow(throwable);

        expect(dataSource.getConnection()).andReturn(connection);
        BaseContext.bindThreadObjectContext(objectContext);

        CayenneTransactionManager transactionManager = new CayenneTransactionManager(dataSource, cayenneRuntime);
        transactionManager.afterPropertiesSet();
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        replay();
        try {
            template.execute(new TransactionCallback<Object>() {

                @Override
                public Object doInTransaction(TransactionStatus status) {
                    //noop
                    return null;
                }
            });
            fail();
        } catch (TransactionSystemException exception) {
            assertNotNull(exception.getCause());
            assertEquals(CayenneValidationException.class, exception.getOriginalException().getClass());
        }
        verify();

        assertNotNull(transactionManager.getResourceFactory());
        assertTrue(transactionManager.getResourceFactory() instanceof DataSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dataIsMissing() {
        replay();
        CayenneTransactionManager transactionManager = new CayenneTransactionManager();
        transactionManager.afterPropertiesSet();
    }

    @Test
    public void useTransactionAwareDataSourceProxy() throws SQLException {

        expect(dataSource.getConnection()).andReturn(connection);
        BaseContext.bindThreadObjectContext(objectContext);

        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);

        CayenneTransactionManager transactionManager = new CayenneTransactionManager(proxy, cayenneRuntime);
        transactionManager.afterPropertiesSet();
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        replay();
        template.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                //noop
                return null;
            }
        });
        verify();
    }

    @Test
    public void useConnectIsAutoCommit() throws SQLException {

        expect(connection.getAutoCommit()).andReturn(true);
        expect(dataSource.getConnection()).andReturn(connection);
        BaseContext.bindThreadObjectContext(objectContext);

        CayenneTransactionManager transactionManager = new CayenneTransactionManager(dataSource, cayenneRuntime);
        transactionManager.afterPropertiesSet();
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        replay();
        template.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                //noop
                return null;
            }
        });
        verify();
    }

    @SuppressWarnings("serial")
    @Test
    public void rollbackTransactionRequiredNew() throws SQLException {

        /*
         * create DataContext for RequiredNew Transaction
         */
        DataContext dataContextRequirednew = mock(DataContext.class);
        CayenneRuntimeException throwable = new CayenneRuntimeException();

        DataChannel channelParent = mock(DataChannel.class);
        expect(objectContext.getChannel()).andReturn(channelParent).once();
        expect(cayenneRuntime.newContext(channelParent)).andReturn(dataContextRequirednew).once();

        dataContextRequirednew.commitChanges();
        expectLastCall().andThrow(throwable);

        Connection cnct = mock(Connection.class);
        expect(dataSource.getConnection()).andReturn(cnct).once();
        cnct = mock(Connection.class);
        expect(dataSource.getConnection()).andReturn(cnct).once();
        BaseContext.bindThreadObjectContext(objectContext);

        final CayenneTransactionManager transactionManager = new CayenneTransactionManager(dataSource, cayenneRuntime) {

        };
        transactionManager.afterPropertiesSet();
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        replay();
        try {
            template.execute(new TransactionCallback<Object>() {

                @Override
                public Object doInTransaction(TransactionStatus status) {
                    TransactionTemplate templateRequiredNew = new TransactionTemplate(transactionManager);
                    templateRequiredNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    templateRequiredNew.execute(new TransactionCallback<Object>() {

                        @Override
                        public Object doInTransaction(TransactionStatus status) {
                            return null;
                        }

                    });
                    return null;
                }
            });
        } catch (TransactionSystemException exception) {
            assertNotNull(exception.getCause());
            assertEquals(throwable, exception.getOriginalException().getCause());
        }
        verify();
    }

    public static class Foo {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
