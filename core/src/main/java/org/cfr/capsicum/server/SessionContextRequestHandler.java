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

public class SessionContextRequestHandler implements RequestHandler, ISessionState {

    private boolean forceNewSession = true;

    static final String SESSION_CONTEXT_KEY = SessionContextRequestHandler.class.getName()
            + ".SESSION_CONTEXT";

    // using injector to lookup services instead of injecting them directly for lazy
    // startup and "late binding"
    @Inject
    private Injector injector;

    @Override
    public void requestStart(ServletRequest request, ServletResponse response) {

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
    public void requestEnd(ServletRequest request, ServletResponse response) {
        CayenneRuntime.bindThreadInjector(null);
        BaseContext.bindThreadObjectContext(null);
    }

    @Override
    public boolean isForceNewSession() {
        return forceNewSession;
    }

    @Override
    public void setForceNewSession(boolean forceNewSession) {
        this.forceNewSession = forceNewSession;
    }
}