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
 * This class allows creating {@link ObjectContext} for the the current thread.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 * @see MethodInterceptor
 */
public class CayenneInterceptor implements MethodInterceptor, DisposableBean {

    /**
     * cayenne runtime instance.
     */
    protected final CayenneRuntime cayenneRuntime;

    /**
     * Creates CayenneInterceptor for CayenneRuntime.
     * @param cayenneRuntime cayenne runtime instance
     */
    public CayenneInterceptor(final CayenneRuntime cayenneRuntime) {
        this.cayenneRuntime = Assert.notNull(cayenneRuntime, "Cayenne Runtime is required");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
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
