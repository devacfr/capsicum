package org.cfr.capsicum.core;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.access.DataContext;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.core.CayenneAccessor;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;



public class CayenneAccessorTest extends EasyMockTestCase {

    @Test
    public void defaultTranslatorInitilized() {
        ICayenneRuntimeContext context = mock(ICayenneRuntimeContext.class);
        CayenneAccessor cayenneAccessor = new CayenneAccessor(context) {};

        assertNotNull(cayenneAccessor.getJdbcExceptionTranslator());
    }

    @Test()
    public void cayenneDataContextNotInitialized() {
        ICayenneRuntimeContext context = mock(ICayenneRuntimeContext.class);
        CayenneAccessor cayenneAccessor = new CayenneAccessor(context) {};
        DataContext objectContext = mock(DataContext.class);
        expect(context.createObjectContext()).andReturn(objectContext).once();
        replay();
        assertEquals(objectContext, cayenneAccessor.getObjectContext());
        verify();
    }

    @Test()
    public void cayenneDataContextInitialized() {
        ICayenneRuntimeContext context = mock(ICayenneRuntimeContext.class);
        CayenneAccessor cayenneAccessor = new CayenneAccessor(context) {};

        DataContext objectContext = mock(DataContext.class);
        BaseContext.bindThreadObjectContext(objectContext);

        replay();

        assertEquals(objectContext, cayenneAccessor.getObjectContext());

        verify();
    }
}