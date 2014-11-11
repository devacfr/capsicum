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
package org.cfr.capsicum.test;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.test.support.CayenneTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.util.Log4jConfigurer;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(CayenneTestExecutionListener.class)
public abstract class AbstractCayenneJUnit4SpringContextTests extends AbstractJUnit4SpringContextTests {

    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @BeforeTransaction
    public void beforeTransaction() throws Exception {
        getCayenneRuntimeContext().updateDatabaseSchema();
    }

    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = Logger.getLogger(getClass());

    public AbstractCayenneJUnit4SpringContextTests() {
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> cl, String beanName) {
        return (T) this.applicationContext.getBean(beanName);
    }

    protected ICayenneRuntimeContext getCayenneRuntimeContext() {
        CayenneRuntimeContext annotation = this.getClass().getAnnotation(CayenneRuntimeContext.class);
        if (annotation == null) {
            throw new IllegalStateException("annotation @CayenneRuntimeContext must be declared on test class.");
        }
        return getBean(ICayenneRuntimeContext.class, "&"
                + annotation.name());
    }

}
