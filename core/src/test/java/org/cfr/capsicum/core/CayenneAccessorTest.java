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
package org.cfr.capsicum.core;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.access.DataContext;
import org.cfr.capsicum.ICayenneRuntimeContext;
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