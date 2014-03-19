package org.cfr.capsicum.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.configuration.web.StatelessContextRequestHandler;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Module;
import org.cfr.capsicum.server.WebInterceptor;
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
        CayenneRuntime cayenneRuntime = new CayenneRuntime(module) {};
        WebInterceptor interceptor = new WebInterceptor(cayenneRuntime);
        assertTrue(interceptor.preHandle(request, response, null));
        interceptor.afterCompletion(request, response, null, null);
        cayenneRuntime.shutdown();
    }
}