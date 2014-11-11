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
package org.cfr.capsicum.spring.datasource;

import java.sql.Connection;

import javax.annotation.Nonnull;

import org.apache.cayenne.ObjectContext;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
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
    public CayenneConnectionHolder(@Nonnull final Connection connection, boolean transactionActive) {
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
    public CayenneConnectionHolder(final Connection connection, final ObjectContext baseContext) {
        super(connection);
        this.baseContext = baseContext;
    }

    /**
     * Create a new ConnectionHolder for the given ConnectionHandle.
     * @param connectionHandle the ConnectionHandle to hold
     */
    public CayenneConnectionHolder(final ConnectionHandle connectionHandle, final ObjectContext baseContext) {
        super(connectionHandle);
        this.baseContext = baseContext;
    }

    @Nonnull
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
    protected void setConnection(@Nonnull final Connection connection) {
        super.setConnection(connection);
    }

    public void setObjectContext(@Nonnull final ObjectContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTransactionActive(final boolean transactionActive) {
        super.setTransactionActive(transactionActive);
    }
}