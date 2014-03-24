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
package org.cfr.capsicum.test.support;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.test.CayenneRuntimeContext;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * @author devacfr
 *
 */
public class CayenneTestExecutionListener extends AbstractTestExecutionListener {

    private static final Log logger = LogFactory.getLog(CayenneTestExecutionListener.class);

    protected ICayenneRuntimeContext context;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        super.prepareTestInstance(testContext);

    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);
        initializeBeforeTestMethod(testContext);
        if (getCayenneRuntimeContext() != null) {
            ObjectContext objectContext = null;
            try {
                objectContext = BaseContext.getThreadObjectContext();
            } catch (IllegalStateException e) {
                objectContext = getCayenneRuntimeContext().createObjectContext();
                BaseContext.bindThreadObjectContext(objectContext);
            }
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
        if (getCayenneRuntimeContext() != null) {
            BaseContext.bindThreadObjectContext(null);
        }
    }

    public ICayenneRuntimeContext getCayenneRuntimeContext() {
        return context;
    }

    private void initializeBeforeTestMethod(TestContext testContext) throws BeansException, Exception {
        Object instance = testContext.getTestInstance();
        Class<?> clazz = instance.getClass();
        CayenneRuntimeContext config = AnnotationUtils.findAnnotation(clazz, CayenneRuntimeContext.class);

        if (config == null) {
            if (logger.isInfoEnabled()) {
                logger.info("@CayenneContextConfiguration is not present for class ["
                        + clazz + "]: using defaults.");
            }
        } else {
            String name = config.name();
            context = testContext.getApplicationContext().getBean("&"
                    + name, ICayenneRuntimeContext.class);
        }
    }
}
