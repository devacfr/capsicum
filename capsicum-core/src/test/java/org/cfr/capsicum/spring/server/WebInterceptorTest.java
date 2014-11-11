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
package org.cfr.capsicum.spring.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.configuration.web.StatelessContextRequestHandler;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Module;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;

public class WebInterceptorTest extends EasyMockTestCase {

    @Test
    public void testDomain() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        final org.apache.cayenne.configuration.ObjectContextFactory contextFactory = mock(org.apache.cayenne.configuration.ObjectContextFactory.class);

        Module module = new Module() {

            @Override
            public void configure(Binder binder) {
                binder.bind(RequestHandler.class).to(StatelessContextRequestHandler.class);
                binder.bind(org.apache.cayenne.configuration.ObjectContextFactory.class).toInstance(contextFactory);
            }
        };
        replay();
        CayenneRuntime cayenneRuntime = new CayenneRuntime(module) {};
        WebInterceptor interceptor = new WebInterceptor(cayenneRuntime);
        assertTrue(interceptor.preHandle(request, response, null));
        interceptor.afterCompletion(request, response, null, null);
        cayenneRuntime.shutdown();
    }
}