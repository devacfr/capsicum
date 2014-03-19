package org.cfr.capsicum.server;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.configuration.web.SessionContextRequestHandler;
import org.apache.cayenne.configuration.web.StatelessContextRequestHandler;
import org.cfr.commons.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Cayenne Spring web request interceptor that wraps Cayenne Runtime,
 * calling the request handler injected in Cayenne DI.
 * @see RequestHandler
 * @see SessionContextRequestHandler
 * @see StatelessContextRequestHandler
 * @since 1.0
 * @author devacfr
 */
public class WebInterceptor implements HandlerInterceptor {

    protected CayenneRuntime cayenneRuntime;

    /**
     * Constructs WebInterceptor with cayenne runtime.
     */
    public WebInterceptor(@Nonnull CayenneRuntime cayenneRuntime) {
        this.cayenneRuntime = Assert.notNull(cayenneRuntime, "Cayenne runtime is required");
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        RequestHandler requestHandler = cayenneRuntime.getInjector().getInstance(RequestHandler.class);
        requestHandler.requestEnd(request, response);
    }

    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // I guess it is too early to dispose of thread DataContext here
    }

    /**
     * Invoke the the cayenne request Handler.
     * @see RequestHandler
     * @see SessionContextRequestHandler
     * @see StatelessContextRequestHandler
     */
    @Override
    public boolean
            preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, Object handler)
                    throws Exception {
        RequestHandler requestHandler = cayenneRuntime.getInjector().getInstance(RequestHandler.class);
        requestHandler.requestStart(request, response);
        return true;
    }
}