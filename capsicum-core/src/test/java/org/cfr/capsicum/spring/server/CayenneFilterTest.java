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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.web.RequestHandler;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.spring.server.CayenneFilter;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Before;
import org.junit.Test;

public class CayenneFilterTest extends EasyMockTestCase {

    private ICayenneRuntimeContext cayenneRuntime;

    private RequestHandler requestHandler;

    private HttpServletRequest request = mock(HttpServletRequest.class);

    private HttpServletResponse response = mock(HttpServletResponse.class);

    private FilterChain filterChain = mock(FilterChain.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        cayenneRuntime = mock(ICayenneRuntimeContext.class);
        requestHandler = mock(RequestHandler.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        expect(request.getContextPath()).andReturn("http://foo.com/service/active.do").anyTimes();
        expect(request.getServletPath()).andReturn("/service").anyTimes();
        expect(request.getRequestURI()).andReturn("/service/active.do").anyTimes();
    }

    @Test
    public void defaultConfiguration() throws ServletException, IOException {

        requestHandler.requestStart(request, response);
        expectLastCall().once();
        requestHandler.requestEnd(request, response);
        expectLastCall().once();

        expect(cayenneRuntime.getInstance(RequestHandler.class)).andReturn(requestHandler).once();
        CayenneFilter cayenneFilter = new CayenneFilter(cayenneRuntime);
        replay();
        cayenneFilter.afterPropertiesSet();
        cayenneFilter.doFilter(request, response, filterChain);
        verify();
    }

    @Test
    public void includeRequestInConditionPattern() throws ServletException, IOException {

        requestHandler.requestStart(request, response);
        expectLastCall().once();
        requestHandler.requestEnd(request, response);
        expectLastCall().once();

        expect(cayenneRuntime.getInstance(RequestHandler.class)).andReturn(requestHandler).once();
        CayenneFilter cayenneFilter = new CayenneFilter(cayenneRuntime);
        cayenneFilter.setIncludeFilterPatterns("**/*.do");
        replay();
        cayenneFilter.afterPropertiesSet();
        cayenneFilter.doFilter(request, response, filterChain);
        verify();
    }

    @Test
    public void noIncludeRequestInConditionPattern() throws ServletException, IOException {

        CayenneFilter cayenneFilter = new CayenneFilter(cayenneRuntime);
        cayenneFilter.setIncludeFilterPatterns("**/*.html");
        replay();
        cayenneFilter.afterPropertiesSet();
        cayenneFilter.doFilter(request, response, filterChain);
        verify();
    }

    @Test
    public void excludeRequestInConditionPattern() throws ServletException, IOException {

        CayenneFilter cayenneFilter = new CayenneFilter(cayenneRuntime);
        cayenneFilter.setExcludeFilterPatterns("**/*.do");
        replay();
        cayenneFilter.afterPropertiesSet();
        cayenneFilter.doFilter(request, response, filterChain);
        verify();
    }
}
