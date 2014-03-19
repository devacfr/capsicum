package org.cfr.capsicum.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Injector;
import org.cfr.commons.util.Assert;
import org.springframework.beans.factory.DisposableBean;

/**
 * 
 * @author devacfr
 * @since 1.0
 *
 */
public class CayenneInterceptor implements MethodInterceptor, DisposableBean {

    protected final CayenneRuntime cayenneRuntime;

    /**
     * Creates CayenneInterceptor for CayenneRuntime.
     */
    public CayenneInterceptor(final CayenneRuntime cayenneRuntime) {
        this.cayenneRuntime = Assert.notNull(cayenneRuntime, "Cayenne Runtime is required");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ObjectContext objectContext = BaseContext.getThreadObjectContext();
        if (objectContext == null) {
            Injector injector = cayenneRuntime.getInjector();
            objectContext = injector.getInstance(ObjectContextFactory.class).createContext();
            BaseContext.bindThreadObjectContext(objectContext);
        }
        Object retVal = null;
        // This is an around advice: Invoke the next interceptor in the chain.
        // This will normally result in a target object being invoked.
        retVal = invocation.proceed();

        return retVal;
    }

    @Override
    public void destroy() throws Exception {
        CayenneRuntime.bindThreadInjector(null);
        BaseContext.bindThreadObjectContext(null);
    }

}
