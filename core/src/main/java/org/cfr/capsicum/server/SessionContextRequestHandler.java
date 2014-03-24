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
package org.cfr.capsicum.server;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Injector;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class SessionContextRequestHandler implements RequestHandler, ISessionState {

    private boolean forceNewSession = true;

    static final String SESSION_CONTEXT_KEY = SessionContextRequestHandler.class.getName() + ".SESSION_CONTEXT";

    /**
     * using injector to lookup services instead of injecting them directly for lazy startup and "late binding".
     */
    @Inject
    private Injector injector;

    @Override
    public void requestStart(final ServletRequest request, final ServletResponse response) {

        CayenneRuntime.bindThreadInjector(injector);

        if (request instanceof HttpServletRequest) {

            HttpSession session = ((HttpServletRequest) request).getSession(this.forceNewSession);

            ObjectContext context = null;
            if (session == null) {
                context = injector.getInstance(ObjectContextFactory.class).createContext();
            } else {
                synchronized (session) {
                    context = (ObjectContext) session.getAttribute(SESSION_CONTEXT_KEY);

                    if (context == null) {
                        context = injector.getInstance(ObjectContextFactory.class).createContext();
                        session.setAttribute(SESSION_CONTEXT_KEY, context);
                    }
                }
            }

            BaseContext.bindThreadObjectContext(context);
        }
    }

    @Override
    public void requestEnd(final ServletRequest request, final ServletResponse response) {
        CayenneRuntime.bindThreadInjector(null);
        BaseContext.bindThreadObjectContext(null);
    }

    @Override
    public boolean isForceNewSession() {
        return forceNewSession;
    }

    @Override
    public void setForceNewSession(final boolean forceNewSession) {
        this.forceNewSession = forceNewSession;
    }
}