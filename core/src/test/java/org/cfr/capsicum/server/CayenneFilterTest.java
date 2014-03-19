package org.cfr.capsicum.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.web.RequestHandler;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.server.CayenneFilter;
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
