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
package org.cfr.capsicum.support;

import java.sql.SQLException;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.OptimisticLockException;
import org.apache.cayenne.exp.ExpressionException;
import org.cfr.capsicum.spring.support.CayenneExpressionException;
import org.cfr.capsicum.spring.support.CayenneOperationException;
import org.cfr.capsicum.spring.support.CayenneOptimisticLockingFailureException;
import org.cfr.capsicum.spring.support.ExceptionTranslator;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import com.google.common.collect.ImmutableMap;

public class ExceptionTranslatorTest extends EasyMockTestCase {

    @Test
    public void jdbcExceptionTranslator() {

        ExceptionTranslator translator = new ExceptionTranslator();
        SQLExceptionTranslator sqlTranslator = mock(SQLExceptionTranslator.class);
        replay();

        translator.setJdbcExceptionTranslator(sqlTranslator);
        assertSame(sqlTranslator, translator.getJdbcExceptionTranslator());
    }

    @Test
    public void convertExpressionException() {
        ExpressionException ex = new ExpressionException("test expression exception");

        ExceptionTranslator translator = new ExceptionTranslator();

        Exception converted = translator.convertAccessException(ex);
        assertTrue(converted instanceof CayenneExpressionException);
    }

    @Test
    public void convertOptimistickLockException() {
        OptimisticLockException ex = new OptimisticLockException(null, null, "query sql", ImmutableMap.of());
        ExceptionTranslator translator = new ExceptionTranslator();

        Exception converted = translator.convertAccessException(ex);
        assertTrue(converted instanceof CayenneOptimisticLockingFailureException);
    }

    @Test
    public void convertNestedException() {
        Exception ex = new Exception("test nested exception");
        ExceptionTranslator translator = new ExceptionTranslator();

        Exception converted = translator.convertAccessException(new CayenneRuntimeException(ex));
        assertTrue(converted instanceof CayenneOperationException);
        assertSame(ex, converted.getCause());
    }

    @Test
    public void convertJdbcException() {
        SQLException ex = new SQLException("test SQL exception");
        ExceptionTranslator translator = new ExceptionTranslator();

        Exception converted = translator.convertJdbcAccessException(ex);
        assertNotNull(converted);
        assertSame(ex, converted.getCause());
    }

    @Test
    public void testConvertNestedJdbcException() {
        SQLException ex = new SQLException("test nested SQL exception");
        ExceptionTranslator translator = new ExceptionTranslator();

        Exception converted = translator.convertAccessException(new CayenneRuntimeException(ex));
        assertNotNull(converted);
        assertSame(ex, converted.getCause());
    }
}
