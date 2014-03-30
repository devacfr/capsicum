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

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.cfr.commons.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Cayenne Spring web request interceptor that wraps Cayenne Runtime,
 * calling the request handler injected in Cayenne DI.
 * @see RequestHandler
 * @see org.apache.cayenne.configuration.web.SessionContextRequestHandler
 * @see org.apache.cayenne.configuration.web.StatelessContextRequestHandler
 * @since 1.0
 * @author devacfr
 */
public class WebInterceptor implements HandlerInterceptor {

    /**
     * Cayenne runtime instance.
     */
    protected CayenneRuntime cayenneRuntime;

    /**
     * Constructs WebInterceptor with cayenne runtime.
     * @param cayenneRuntime caynne runtime instance
     */
    public WebInterceptor(@Nonnull final CayenneRuntime cayenneRuntime) {
        this.cayenneRuntime = Assert.notNull(cayenneRuntime, "Cayenne runtime is required");
    }

    @Override
    public void afterCompletion(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response,
                                final Object handler, final Exception ex) throws Exception {
        RequestHandler requestHandler = cayenneRuntime.getInjector().getInstance(RequestHandler.class);
        requestHandler.requestEnd(request, response);
    }

    @Override
    public void postHandle(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response,
                           final Object handler, final ModelAndView modelAndView) throws Exception {
        // I guess it is too early to dispose of thread DataContext here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response,
                             final Object handler) throws Exception {
        RequestHandler requestHandler = cayenneRuntime.getInjector().getInstance(RequestHandler.class);
        requestHandler.requestStart(request, response);
        return true;
    }
}