package org.cfr.capsicum.support;

import java.sql.SQLException;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.OptimisticLockException;
import org.apache.cayenne.exp.ExpressionException;
import org.cfr.capsicum.support.CayenneExpressionException;
import org.cfr.capsicum.support.CayenneOperationException;
import org.cfr.capsicum.support.CayenneOptimisticLockingFailureException;
import org.cfr.capsicum.support.ExceptionTranslator;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import com.google.common.collect.ImmutableMap;

public class ExceptionTranslatorTest extends EasyMockTestCase {

    @Test
    public void jdbcExceptionTranslator() {

        ExceptionTranslator translator = new ExceptionTranslator();
        SQLExceptionTranslator sqlTranslator = mock(SQLExceptionTranslator.class);

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
