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
package org.cfr.capsicum.spring.access;

import java.sql.SQLException;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.OperationObserver;
import org.apache.cayenne.query.Query;
import org.cfr.capsicum.spring.datasource.TransactionAwareDataSourceProxy;

/**
 * This class extends {@link DataNode} but remove all transaction action.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class SpringDataNode extends DataNode {

    /**
     * Create new default instance.
     */
    public SpringDataNode() {
        super();
    }

    /**
     * 
     * @param name the datanode name
     */
    public SpringDataNode(@Nullable final String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataSource(@Nonnull final DataSource dataSource) {
        this.dataSource = createTransactionAwareDataSource(dataSource);
    }

    /**
     * create a datasource  participating in Spring-managed transactions.
     * @param ds datasource
     * @return Returns new Datasource if necessary, adding awareness of Spring-managed transactions.
     */
    @Nonnull
    protected DataSource createTransactionAwareDataSource(@Nullable final DataSource ds) {
        if (ds != null) {
            return new TransactionAwareDataSourceProxy(ds);
        }
        return ds;
    }

    @Override
    public void performQueries(@Nonnull final Collection<? extends Query> queries,
                               @Nonnull final OperationObserver callback) {
        if (schemaUpdateStrategy != null) {
            try {
                schemaUpdateStrategy.updateSchema(this);
            } catch (SQLException e) {
                new CayenneRuntimeException(e);
            }
        }
        // TODO [devacfr] do not user super methohs, but put here only useful code 
        super.performQueries(queries, callback);
    }
}
