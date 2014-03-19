package org.cfr.capsicum.core;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.core.CayenneTemplate;
import org.cfr.capsicum.core.ICayenneCallback;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;
import org.springframework.dao.DataAccessException;


public class CayenneTemplateTest extends EasyMockTestCase {

    protected boolean executed;

    @Test
    public void testExecute() {
        @SuppressWarnings("serial")
        final ObjectContext testContext = new DataContext() {

            @Override
            public void rollbackChanges() {
            }

            @Override
            public void commitChanges() throws CayenneRuntimeException {
            }
        };
        executed = false;
        BaseContext.bindThreadObjectContext(testContext);
        ICayenneRuntimeContext context = mock(ICayenneRuntimeContext.class);
        CayenneTemplate cayenneTemplate = new CayenneTemplate(context);

        ICayenneCallback callback = new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                assertSame(testContext, context);
                executed = true;
                return null;
            }
        };

        BaseContext.bindThreadObjectContext(testContext);
        try {
            cayenneTemplate.execute(callback);
            assertTrue("Template failed to execute callback", executed);
        } finally {
            BaseContext.bindThreadObjectContext(null);
        }
    }

    @Test
    public void testExecuteWrapExceptions() {
        final CayenneRuntimeException exception = new CayenneRuntimeException("test CRE");
        executed = false;

        ICayenneRuntimeContext context = mock(ICayenneRuntimeContext.class);
        CayenneTemplate cayenneTemplate = new CayenneTemplate(context);

        ICayenneCallback callback = new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                throw exception;
            }
        };
        @SuppressWarnings("serial")
        final ObjectContext testContext = new DataContext() {

            @Override
            public void rollbackChanges() {
            }

            @Override
            public void commitChanges() throws CayenneRuntimeException {
            }
        };

        BaseContext.bindThreadObjectContext(testContext);
        try {
            cayenneTemplate.execute(callback);
            fail("CayenneRuntimeException wasn't rethrown.");
        } catch (DataAccessException dae) {
            assertSame(exception, dae.getCause());
        } finally {
            BaseContext.bindThreadObjectContext(null);
        }
    }
}