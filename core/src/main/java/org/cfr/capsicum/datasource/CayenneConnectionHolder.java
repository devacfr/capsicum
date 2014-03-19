package org.cfr.capsicum.datasource;

import java.sql.Connection;

import org.apache.cayenne.ObjectContext;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;

public class CayenneConnectionHolder extends ConnectionHolder {

    private ObjectContext baseContext;

    /**
     * Create a new ConnectionHolder for the given JDBC Connection,
     * wrapping it with a {@link SimpleConnectionHandle}.
     * @param connection the JDBC Connection to hold
     * @param transactionActive whether the given Connection is involved
     * in an ongoing transaction
     * @see SimpleConnectionHandle
     */
    public CayenneConnectionHolder(Connection connection, boolean transactionActive) {
        super(connection, transactionActive);
    }

    /**
     * Create a new ConnectionHolder for the given JDBC Connection,
     * wrapping it with a {@link SimpleConnectionHandle},
     * assuming that there is no ongoing transaction.
     * @param connection the JDBC Connection to hold
     * @see SimpleConnectionHandle
     * @see #ConnectionHolder(java.sql.Connection, boolean)
     */
    public CayenneConnectionHolder(Connection connection, ObjectContext baseContext) {
        super(connection);
        this.baseContext = baseContext;
    }

    /**
     * Create a new ConnectionHolder for the given ConnectionHandle.
     * @param connectionHandle the ConnectionHandle to hold
     */
    public CayenneConnectionHolder(ConnectionHandle connectionHandle, ObjectContext baseContext) {
        super(connectionHandle);
        this.baseContext = baseContext;
    }

    public ObjectContext getObjectContext() {
        return baseContext;
    }

    @Override
    protected boolean hasConnection() {
        return super.hasConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransactionActive() {
        return super.isTransactionActive();
    }

    @Override
    protected void setConnection(Connection connection) {
        super.setConnection(connection);
    }

    public void setObjectContext(ObjectContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTransactionActive(boolean transactionActive) {
        super.setTransactionActive(transactionActive);
    }
}