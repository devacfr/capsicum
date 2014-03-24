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
package org.cfr.capsicum.configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.dba.DbAdapter;
import org.cfr.capsicum.resource.SpringResource;
import org.cfr.commons.util.Assert;
import org.springframework.core.io.Resource;
import org.springframework.transaction.support.ResourceTransactionManager;

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
public class DataDomainDefinition {

    /**
     * the Resource file containing the datadomain description. 
     */
    private Resource domainResource;

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
    private ResourceTransactionManager transactionManager;

    /**
     * the specific {@link DbAdapter} class.
     */
    private Class<? extends DbAdapter> adapterClass;

    /**
     * 
     */
    private String name;

    /**
     * Default constructor.
     */
    public DataDomainDefinition() {
    }

    /**
     * Constructs this class wiht the name of domain. the name can not be null or empty.
     * @param name the name of domain.
     */
    public DataDomainDefinition(@Nonnull final String name) {
        this.name = Assert.hasText(name);
    }

    /**
     * Gets the resource location of domain.
     * @return Returns the resource location of domain.
     */
    public Resource getDomainResource() {
        return domainResource;
    }

    /**
     * Sets the resource location of domain.
     * @param resource the resource location of domain.
     */
    public void setDomainResource(@Nonnull final Resource resource) {
        this.domainResource = new SpringResource(resource);
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
     * Gets the datasource. If the datasource property didn't fill, 
     * returns the datasource associated to transaction manager 
     * @return Returns the filled datasource, otherwise the datasource associated to transaction manager.
     * @see DataDomainDefinition#getTransactionManager() 
     */
    public DataSource getDataSource() {
        if (dataSource == null && transactionManager != null) {
            return (DataSource) transactionManager.getResourceFactory();
        }
        return dataSource;
    }

    /**
     * Sets the datasource.
     * @param ds the datasource
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
    public ResourceTransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * Sets the transaction manager.
     * @param transaction the transaction  manager
     */
    public void setTransactionManager(@Nullable final ResourceTransactionManager transaction) {
        this.transactionManager = transaction;
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
     * Gets the name of domain.
     * @return returns the name of domain.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of domain.
     * @param name name of domain
     */
    public void setName(@Nonnull final String name) {
        this.name = name;
    }
}
