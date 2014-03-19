package org.cfr.capsicum.core;

import java.sql.SQLException;

import javax.annotation.Nonnull;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.datasource.CayenneTransactionManager;
import org.cfr.capsicum.support.ExceptionTranslator;
import org.cfr.commons.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;


/**
 * A superclass for templates or interceptors using Cayenne. Provides Spring
 * exception translation facility.
 * @since 1.0
 * @author devacfr
 */
public abstract class CayenneAccessor implements InitializingBean {

    /**
     * the exception translator.
     */
    private ExceptionTranslator exceptionTranslator;

    /**
     * a cayenne runtime factory.
     */
    private final ICayenneRuntimeContext cayenneRuntimeFactory;

    /**
     * Constructs this accessor to one cayenne runtime context.
     * @param cayenneRuntimeContext one cayenne runtime context
     */
    public CayenneAccessor(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext) {
        this.exceptionTranslator = new ExceptionTranslator();
        this.cayenneRuntimeFactory = Assert.notNull(cayenneRuntimeContext);
    }

    /**
     * Gets the cayenne runtime context.
     * @return Returns the associated cayenne runtime context.
     * @see ICayenneRuntimeContext
     */
    protected ICayenneRuntimeContext getCayenneRuntime() {
        return cayenneRuntimeFactory;
    }

    /**
     * 
     * @return Returns the {@link CayenneTransactionManager} associated to cayenne context runtime.
     */
    protected CayenneTransactionManager getTransactionManager() {
        return cayenneRuntimeFactory.getTransactionManager();
    }

    /**
     * @return  Returns DataContext.
     */
    public ObjectContext getObjectContext() {
        ObjectContext objectContext = null;
        //TODO [devacfr] do difference between server and client
        try {
            objectContext = BaseContext.getThreadObjectContext();
        } catch (IllegalStateException ex) {
            objectContext = cayenneRuntimeFactory.createObjectContext();
            BaseContext.bindThreadObjectContext(objectContext);
        }
        return objectContext;
    }

    /**
     * Eagerly initializes the exception translator, creating a default one if
     * not set.
     */
    @Override
    public void afterPropertiesSet() {
    }

    /**
     * @return Returns the JDBC exception translator for this instance. If not
     * initialized, creates default portable SQLStateSQLExceptionTranslator, as
     * Cayenne configuration can span multiple DataSources,
     */
    public SQLExceptionTranslator getJdbcExceptionTranslator() {
        return exceptionTranslator.getJdbcExceptionTranslator();
    }

    /**
     * Sets the JDBC exception translator for this instance. Applied to
     * SQLExceptions thrown by callback code. The default exception translator
     * is either a SQLStateSQLExceptionTranslator.
     * @param jdbcExceptionTranslator JDBC exception translator. 
     */
    public void setJdbcExceptionTranslator(final SQLExceptionTranslator jdbcExceptionTranslator) {
        exceptionTranslator.setJdbcExceptionTranslator(jdbcExceptionTranslator);
    }

    /**
     * @param ex exception to translate.
     * @return Converts SQLException to an appropriate exception from the
     * org.springframework.dao hierarchy.
     */
    public DataAccessException convertJdbcAccessException(final SQLException ex) {
        return exceptionTranslator.convertJdbcAccessException(ex);
    }

    /**
     * @param ex exception to translate.
     * @return Converts a Cayenne exception (usualy this is CayenneRuntimeException) to
     * an appropriate exception from the org.springframework.dao hierarchy. Will
     * automatically detect wrapped SQLExceptions and convert them accordingly.
     */
    public DataAccessException convertAccessException(final Exception ex) {
        return exceptionTranslator.convertAccessException(ex);
    }

}