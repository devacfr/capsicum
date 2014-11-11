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
package org.cfr.capsicum.access;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.tx.TransactionManager;
import org.cfr.capsicum.ICayenneRuntimeContext;

/**
 * <p>this class allows to override the {@link org.apache.cayenne.configuration.DataNodeDescriptor}
 * properties of cayenne configuration.<br>
 * To allow creating a link between the {@link org.apache.cayenne.access.DataDomain}
 * in the cayenne configuration xml file
 * and this class,
 * the name of domain is mandatory.</p>
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 * @see org.apache.cayenne.configuration.DataNodeDescriptor
 * @see org.apache.cayenne.access.DataDomain
 */
public class CustomDataDomainDefinition extends DataDomainDefinition {

    /**
     * the update strategy associated to this {@link org.apache.cayenne.access.DataDomain}.
     */
    private Class<? extends SchemaUpdateStrategy> schemaUpdateStrategy;

    /**
     * the data source.
     */
    private DataSource dataSource;

    /**
     * the transaction manager.
     */
    private TransactionManager transactionManager;

    /**
     * the specific {@link DbAdapter} class.
     */
    private Class<? extends DbAdapter> adapterClass;

    /**
     * Default constructor.
     */
    public CustomDataDomainDefinition() {
    }

    /**
     * Gets the schema update strategy class. The default value is
     * {@link org.apache.cayenne.access.dbsync.SkipSchemaUpdateStrategy} class
     * @return Returns the schema update strategy class.
     */
    public Class<? extends SchemaUpdateStrategy> getSchemaUpdateStrategy() {
        return schemaUpdateStrategy;
    }

    /**
     * Sets the schema update strategy class.
     * @param strategy the schema update strategy.
     */
    public void setSchemaUpdateStrategy(@Nullable final Class<? extends SchemaUpdateStrategy> strategy) {
        this.schemaUpdateStrategy = strategy;
    }

    /**
     * Gets the data source.
     * @return Returns the filled data source.
     */
    public DataSource getDataSource() {

        return dataSource;
    }

    /**
     * Sets the data source.
     * @param ds the data source
     */
    public void setDataSource(@Nullable final DataSource ds) {
        this.dataSource = ds;
    }

    /**
     * Gets indicating whether a transaction manager is define for this domain.
     * @return Returns <code>true</code> whether a transaction manager is define,
     * otherwise <code>false</code>
     */
    public boolean useExternalTransaction() {
        return this.transactionManager != null;
    }

    /**
     * Gets the transaction Manager related to one DataNode.
     * @return Returns the transaction manager
     */
    public TransactionManager getTransactionManagerName() {
        return transactionManager;
    }

    /**
     * Sets the transaction manager.
     * @param transactionManager the transaction  manager
     */
    public void setTransactionManager(@Nullable final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Sets the specific database adapter class for this domain. if this field is not filled,
     * cayenne will use {@link org.apache.cayenne.dba.AutoAdapter} class by default.
     * @param adapterClass the database adapter class.
     */
    public void setAdapterClass(@Nullable final Class<? extends DbAdapter> adapterClass) {
        this.adapterClass = adapterClass;
    }

    /**
     * Gets the specific database adapter class.
     * @return Returns the specific database adapter class.
     */
    public Class<?> getAdapterClass() {
        return adapterClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext,
                          @Nonnull final DataNodeDescriptor dataNodeDescriptor) {
        super.configure(cayenneRuntimeContext, dataNodeDescriptor);
        if (getSchemaUpdateStrategy() != null) {
            dataNodeDescriptor.setSchemaUpdateStrategyType(getSchemaUpdateStrategy().getName());
        }
        //TODO [devacfr] get the good data source factory
        dataNodeDescriptor.setDataSourceFactoryType(cayenneRuntimeContext.getClass().getName());
        if (getAdapterClass() != null) {
            dataNodeDescriptor.setAdapterType(getAdapterClass().getName());
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends DataDomainDefinition.Builder<CustomDataDomainDefinition> {

        private Class<? extends SchemaUpdateStrategy> schemaUpdateStrategy;

        private DataSource dataSource;

        private TransactionManager transactionManager;

        private Class<? extends DbAdapter> adapterClass;

        /**
         * {@inheritDoc}
         */
        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Builder location(String location) {
            return (Builder) super.location(location);
        }

        public Builder schemaUpdateStrategy(Class<? extends SchemaUpdateStrategy> schemaUpdateStrategy) {
            this.schemaUpdateStrategy = schemaUpdateStrategy;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder transactionManager(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
            return this;
        }

        public Builder adapterClass(Class<? extends DbAdapter> adapterClass) {
            this.adapterClass = adapterClass;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CustomDataDomainDefinition self() {
            return new CustomDataDomainDefinition();
        }

        /**
         *
         */
        @Override
        public CustomDataDomainDefinition build() {
            CustomDataDomainDefinition definition = (CustomDataDomainDefinition) super.build();
            definition.setSchemaUpdateStrategy(this.schemaUpdateStrategy);
            definition.setDataSource(dataSource);
            definition.setTransactionManager(transactionManager);
            definition.setAdapterClass(adapterClass);
            return definition;
        }
    }
}
