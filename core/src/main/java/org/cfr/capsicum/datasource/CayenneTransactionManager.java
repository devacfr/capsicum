package org.cfr.capsicum.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Injector;
import org.cfr.capsicum.support.ExceptionTranslator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;


public class CayenneTransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager, InitializingBean {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private ExceptionTranslator exceptionTranslator;

    private DataSource dataSource;

    private CayenneRuntime cayenneRuntime;

    /**
     * Create a new CayenneTransactionManager instance. A DataSource has to
     * be set to be able to use it.
     * 
     * @see #setDataSource
     */
    public CayenneTransactionManager() {
        setNestedTransactionAllowed(true);
        this.exceptionTranslator = new ExceptionTranslator();
    }

    /**
     * Create a new CayenneTransactionManager instance.
     * 
     * @param dataSource
     *            JDBC DataSource to manage transactions for
     */
    public CayenneTransactionManager(@Nonnull DataSource dataSource, @Nonnull CayenneRuntime cayenneRuntime) {
        this();
        setDataSource(dataSource);
        setCayenneRuntime(cayenneRuntime);
    }

    /**
     * Set the JDBC DataSource that this instance should manage transactions
     * for.
     * <p>
     * This will typically be a locally defined DataSource, for example a
     * Jakarta Commons DBCP connection pool. Alternatively, you can also drive
     * transactions for a non-XA J2EE DataSource fetched from JNDI. For an XA
     * DataSource, use JtaTransactionManager.
     * <p>
     * The DataSource specified here should be the target DataSource to manage
     * transactions for, not a TransactionAwareDataSourceProxy. Only data access
     * code may work with TransactionAwareDataSourceProxy, while the transaction
     * manager needs to work on the underlying target DataSource. If there's
     * nevertheless a TransactionAwareDataSourceProxy passed in, it will be
     * unwrapped to extract its target DataSource.
     * <p>
     * <b>The DataSource passed in here needs to return independent Connections.</b>
     * The Connections may come from a pool (the typical case), but the
     * DataSource must not return thread-scoped / request-scoped Connections or
     * the like.
     * 
     * @see TransactionAwareDataSourceProxy
     * @see org.springframework.transaction.jta.JtaTransactionManager
     */
    public void setDataSource(DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            // If we got a TransactionAwareDataSourceProxy, we need to perform
            // transactions
            // for its underlying target DataSource, else data access code won't
            // see
            // properly exposed transactions (i.e. transactions for the target
            // DataSource).
            this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
        } else {
            this.dataSource = dataSource;
        }
    }

    /**
     * Return the JDBC DataSource that this instance manages transactions for.
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setCayenneRuntime(CayenneRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        if (getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResourceFactory() {
        return getDataSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetTransaction() {
        CayenneTransactionObject txObject = new CayenneTransactionObject();
        txObject.setSavepointAllowed(isNestedTransactionAllowed());
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(this.dataSource);
        txObject.setConnectionHolder(conHolder, false);
        return txObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isExistingTransaction(Object transaction) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) transaction;
        return (txObject.getConnectionHolder() != null && txObject.getConnectionHolderEx().isTransactionActive());
    }

    /**
     * This implementation sets the isolation level but ignores the timeout.
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) transaction;
        Connection con = null;

        try {
            if (txObject.getConnectionHolder() == null || txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
                Connection newCon = this.dataSource.getConnection();
                if (logger.isDebugEnabled()) {
                    logger.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
                }
                ObjectContext context = null;
                if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
                    Injector injector = this.cayenneRuntime.getInjector();
                    context = injector.getInstance(ObjectContextFactory.class).createContext();
                } else {
                    context = BaseContext.getThreadObjectContext();
                }
                txObject.setConnectionHolder(new CayenneConnectionHolder(newCon, context), true);
                BaseContext.bindThreadObjectContext(context);
            }

            txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
            con = txObject.getConnectionHolder().getConnection();

            Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
            txObject.setPreviousIsolationLevel(previousIsolationLevel);

            // Switch to manual commit if necessary. This is very expensive in
            // some
            // JDBC drivers,
            // so we don't want to do it unnecessarily (for example if we've
            // explicitly
            // configured the connection pool to set it already).
            if (con.getAutoCommit()) {
                txObject.setMustRestoreAutoCommit(true);
                if (logger.isDebugEnabled()) {
                    logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
                }
                con.setAutoCommit(false);
            }
            txObject.getConnectionHolderEx().setTransactionActive(true);

            int timeout = determineTimeout(definition);
            if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
                txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
            }

            // Bind the session holder to the thread.
            if (txObject.isNewConnectionHolder()) {
                TransactionSynchronizationManager.bindResource(getDataSource(), txObject.getConnectionHolder());
            }
        }

        catch (SQLException ex) {
            DataSourceUtils.releaseConnection(con, this.dataSource);
            throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction",
                    exceptionTranslator.convertJdbcAccessException(ex));
        }
    }

    @Override
    protected Object doSuspend(Object transaction) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) transaction;
        txObject.setConnectionHolder(null);
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.unbindResource(this.dataSource);
        return conHolder;
    }

    @Override
    protected void doResume(Object transaction, Object suspendedResources) {
        CayenneConnectionHolder conHolder = (CayenneConnectionHolder) suspendedResources;
        TransactionSynchronizationManager.bindResource(this.dataSource, conHolder);
        BaseContext.bindThreadObjectContext(conHolder.getObjectContext());
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (status.isDebug()) {
            logger.debug("Committing JDBC transaction on Connection [" + con + "]");
        }
        try {
            status.flush();
            con.commit();
        } catch (SQLException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", exceptionTranslator.convertJdbcAccessException(ex));
        } catch (CayenneRuntimeException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", exceptionTranslator.convertAccessException(ex));
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        ObjectContext dataContext = txObject.getConnectionHolderEx().getObjectContext();
        if (status.isDebug()) {
            logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
        }
        try {
            if (dataContext != null) {
                dataContext.rollbackChanges();
            }
            con.rollback();
        } catch (SQLException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", exceptionTranslator.convertJdbcAccessException(ex));
        } catch (CayenneRuntimeException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", exceptionTranslator.convertAccessException(ex));
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) status.getTransaction();
        if (status.isDebug()) {
            logger.debug("Setting JDBC transaction [" + txObject.getConnectionHolder().getConnection() + "] rollback-only");
        }
        txObject.setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        CayenneTransactionObject txObject = (CayenneTransactionObject) transaction;

        // Remove the connection holder from the thread, if exposed.
        if (txObject.isNewConnectionHolder()) {
            TransactionSynchronizationManager.unbindResource(this.dataSource);
        }

        // Reset connection.
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            if (txObject.isMustRestoreAutoCommit()) {
                con.setAutoCommit(true);
            }
            DataSourceUtils.resetConnectionAfterTransaction(con, txObject.getPreviousIsolationLevel());
        } catch (Throwable ex) {
            logger.debug("Could not reset JDBC Connection after transaction", ex);
        }

        if (txObject.isNewConnectionHolder()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Releasing JDBC Connection [" + con + "] after transaction");
            }
            DataSourceUtils.releaseConnection(con, this.dataSource);
        }

        txObject.getConnectionHolder().clear();
    }

    /**
     * DataSource transaction object, representing a ConnectionHolder. Used as
     * transaction object by DataSourceTransactionManager.
     */
    static class CayenneTransactionObject extends JdbcTransactionObjectSupport {

        private boolean newConnectionHolder;

        private boolean mustRestoreAutoCommit;

        public void setConnectionHolder(ConnectionHolder connectionHolder, boolean newConnectionHolder) {
            super.setConnectionHolder(connectionHolder);
            this.newConnectionHolder = newConnectionHolder;
        }

        public boolean isNewConnectionHolder() {
            return newConnectionHolder;
        }

        @Deprecated
        public boolean hasTransaction() {
            return (getConnectionHolder() != null && getConnectionHolderEx().isTransactionActive());
        }

        public CayenneConnectionHolder getConnectionHolderEx() {
            return ((CayenneConnectionHolder) getConnectionHolder());
        }

        public void setMustRestoreAutoCommit(boolean mustRestoreAutoCommit) {
            this.mustRestoreAutoCommit = mustRestoreAutoCommit;
        }

        public boolean isMustRestoreAutoCommit() {
            return mustRestoreAutoCommit;
        }

        public void setRollbackOnly() {
            getConnectionHolder().setRollbackOnly();
        }

        @Override
        public boolean isRollbackOnly() {
            return getConnectionHolder().isRollbackOnly();
        }

        @Override
        public void flush() {
            CayenneConnectionHolder connectionHolder = getConnectionHolderEx();
            connectionHolder.getObjectContext().commitChanges();
        }

    }

}