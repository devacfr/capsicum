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

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.OptimisticLockException;
import org.apache.cayenne.exp.ExpressionException;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/**
 * Class Utility allows to convert JDBC and Cayenne exception
 * @author cfriedri
 *
 */
public class ExceptionTranslator {

    private SQLExceptionTranslator jdbcExceptionTranslator;

    /**
     * Returns the JDBC exception translator for this instance. If not
     * initialized, creates default portable SQLStateSQLExceptionTranslator, as
     * Cayenne configuration can span multiple DataSources,
     */
    // TODO: if we can propagate DataNode that caused the exception, we can get
    // a DataSource for exception and use SQLErrorCodeSQLExceptionTranslator.
    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        if (this.jdbcExceptionTranslator == null) {
            this.jdbcExceptionTranslator = new SQLStateSQLExceptionTranslator();
        }
        return this.jdbcExceptionTranslator;
    }

    /**
     * Sets the JDBC exception translator for this instance. Applied to
     * SQLExceptions thrown by callback code. The default exception translator
     * is either a SQLStateSQLExceptionTranslator.
     */
    public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
        this.jdbcExceptionTranslator = jdbcExceptionTranslator;
    }

    /**
     * Converts SQLException to an appropriate exception from the
     * org.springframework.dao hierarchy.
     */
    public DataAccessException convertJdbcAccessException(SQLException ex) {
        return getJdbcExceptionTranslator().translate("Cayenne operation", null, ex);
    }

    /**
     * Converts a Cayenne exception (usualy this is CayenneRuntimeException) to
     * an appropriate exception from the org.springframework.dao hierarchy. Will
     * automatically detect wrapped SQLExceptions and convert them accordingly.
     */
    public DataAccessException convertAccessException(Exception ex) {
        // before unwind, handle ExpressionExceptions
        if (ex instanceof ExpressionException) {
            return new CayenneExpressionException((ExpressionException) ex);
        }

        Throwable th = Util.unwindException(ex);

        // handle SQL Exceptions
        if (th instanceof SQLException) {
            SQLException sqlException = (SQLException) th;
            return convertJdbcAccessException(sqlException);
        }

        if (th instanceof ValidationException) {
            return new CayenneValidationException((ValidationException) th);
        }

        if (th instanceof OptimisticLockException) {
            return new CayenneOptimisticLockingFailureException((OptimisticLockException) th);
        }

        if (th instanceof CayenneRuntimeException) {
            return new CayenneOperationException(((CayenneRuntimeException) th).getUnlabeledMessage(), th);
        }

        if (th instanceof CayenneException) {
            return new CayenneOperationException(((CayenneException) th).getUnlabeledMessage(), th);
        }

        // handle exceptions originated in Cayenne
        return new CayenneOperationException(th.getMessage(), th);
    }
}
