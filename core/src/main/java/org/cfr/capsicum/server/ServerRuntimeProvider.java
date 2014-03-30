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
package org.cfr.capsicum.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.access.dbsync.SkipSchemaUpdateStrategy;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.DataChannelDescriptorLoader;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.configuration.XMLDataChannelDescriptorLoader;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.configuration.web.SessionContextRequestHandler;
import org.apache.cayenne.configuration.web.StatelessContextRequestHandler;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.apache.cayenne.log.JdbcEventLogger;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.ResourceLocator;
import org.apache.cayenne.resource.URLResource;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.DataDomainDefinition;
import org.cfr.capsicum.access.DataDomainProvider;
import org.cfr.capsicum.configuration.DecoratorDataChannelDescriptorLoader;
import org.cfr.capsicum.log.Slf4jJdbcEventLogger;
import org.cfr.commons.util.Assert;
import org.cfr.commons.util.ResourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A singleton factory for Cayenne runtime.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class ServerRuntimeProvider implements Provider<CayenneRuntime>, ICayenneRuntimeContext {

    protected ServerRuntime cayenneRuntime;

    private List<DataDomainDefinition> dataDomainDefinitions;

    private Class<SchemaUpdateStrategy> defaultSchemaUpdateStrategy;

    private boolean useSessionPersistentState = true;

    private DataSource dataSource;

    /**
     *
     */
    public ServerRuntimeProvider() {

    }

    /**
     *
     * @param dataSource
     */
    public ServerRuntimeProvider(@Nonnull final DataSource dataSource) {
        this.dataSource = Assert.notNull(dataSource, "datasource is required");
    }

    /**
     * Builds Cayenne configuration object based on configured properties.
     * @exception Exception if there is error.
     */
    @PostConstruct
    public void afterPropertiesSet() throws Exception {

        ServerModule springModule = new ServerModule();
        List<String> configurationLocations = ImmutableList.of();
        if (dataDomainDefinitions != null
                && !dataDomainDefinitions.isEmpty()) {
            configurationLocations = Lists.newArrayListWithCapacity(dataDomainDefinitions.size());
            for (DataDomainDefinition dataDomainDefinition : dataDomainDefinitions) {
                Assert.notNull(dataDomainDefinition.getName(), "the name of DataDomain is required");
                Resource resource = new URLResource(
                    ResourceUtils.getURL(Assert.notNull(dataDomainDefinition.getLocation(),
                        "the location of DataDomain is required")));
                configurationLocations.add(resource.getURL().toString());
            }
        }

        // create cayenne runtime instance with domain definitions
        cayenneRuntime = new ServerRuntime(Iterables.toArray(configurationLocations, String.class), springModule);

    }

    /**
     * Shuts down underlying Configuration.
     * @exception Exception if cayenne shutdown failed
     */
    @PreDestroy
    public void destroy() throws Exception {
        if (cayenneRuntime != null) {
            cayenneRuntime.shutdown();
        }
        CayenneRuntime.bindThreadInjector(null);
        BaseContext.bindThreadObjectContext(null);
    }

    @Override
    public CayenneRuntime get() {
        return this.cayenneRuntime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataDomain createDataDomain(@Nonnull final String name) {
        return new DataDomain(Assert.notNull(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataNode createDataNode(@Nonnull final String name) {
        return new DataNode(Assert.hasText(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectContext createObjectContext() {
        Assert.notNull(cayenneRuntime);
        return cayenneRuntime.newContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataDomain getDataDomain() {
        Assert.notNull(cayenneRuntime);
        return cayenneRuntime.getDataDomain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstance(final Class<T> classType) {
        return this.cayenneRuntime.getInjector().getInstance(classType);
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

    /**
     *
     * @return
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     *
     * @return
     */
    public boolean isUseSessionPersistentState() {
        return useSessionPersistentState;
    }

    /**
     *
     * @param useSessionPersistentState
     */
    public void setUseSessionPersistentState(boolean useSessionPersistentState) {
        this.useSessionPersistentState = useSessionPersistentState;
    }

    @Override
    public void updateDatabaseSchema() throws SQLException {
        DataDomain domain = getDataDomain();
        if (domain == null) {
            return;
        }
        Collection<DataNode> dataNodes = domain.getDataNodes();
        for (DataNode dataNode : dataNodes) {
            SchemaUpdateStrategy strategy = dataNode.getSchemaUpdateStrategy();
            strategy.updateSchema(dataNode);
        }

    }

    @Nonnull
    public JdbcEventLogger getJdbcEventLogger() {
        return getInstance(JdbcEventLogger.class);
    }

    /**
     *
     * @return
     */
    @Override
    @Nonnull
    public List<DataDomainDefinition> getDataDomainDefinitions() {
        return dataDomainDefinitions;
    }

    /**
     *
     * @param dataDomainDefinitions
     */
    public void setDataDomainDefinitions(@Nonnull List<DataDomainDefinition> dataDomainDefinitions) {
        this.dataDomainDefinitions = dataDomainDefinitions;
    }

    /**
     *
     * @param defaultSchemaUpdateStrategy
     */
    public void setDefaultSchemaUpdateStrategy(@Nonnull Class<SchemaUpdateStrategy> defaultSchemaUpdateStrategy) {
        this.defaultSchemaUpdateStrategy = defaultSchemaUpdateStrategy;
    }

    /**
     *
     * @return
     */
    public Class<? extends SchemaUpdateStrategy> getDefaultSchemaUpdateStrategy() {
        return defaultSchemaUpdateStrategy != null ? defaultSchemaUpdateStrategy : SkipSchemaUpdateStrategy.class;
    }

    private class ServerModule implements Module {

        @Override
        public void configure(@Nonnull final Binder binder) {
            // replace Default DataDomain Provider
            binder.bind(JdbcEventLogger.class).to(Slf4jJdbcEventLogger.class);
            binder.bind(DataDomain.class).toProvider(DataDomainProvider.class);
            binder.bind(ICayenneRuntimeContext.class).toInstance(ServerRuntimeProvider.this);
            binder.bind(ResourceLocator.class).to(DefaultResourceLocator.class);

            binder.bind(Key.get(DataChannelDescriptorLoader.class, "DefaultXMLDataChannelDescriptorLoader"))
            .to(XMLDataChannelDescriptorLoader.class);
            binder.bind(DataChannelDescriptorLoader.class).toInstance(new DecoratorDataChannelDescriptorLoader(
                ServerRuntimeProvider.this));
            binder.bind(ObjectContextFactory.class).to(DataContextFactory.class);

            if (getDefaultSchemaUpdateStrategy() != null) {
                binder.bind(SchemaUpdateStrategy.class).to(getDefaultSchemaUpdateStrategy());
            }

            if (isUseSessionPersistentState()) {
                binder.bind(RequestHandler.class).to(SessionContextRequestHandler.class).withoutScope();
            } else {
                binder.bind(RequestHandler.class).to(StatelessContextRequestHandler.class).withoutScope();
            }
        }
    }

    private static class DefaultResourceLocator implements ResourceLocator {

        /**
         *
         */
        @Override
        public Collection<Resource> findResources(String name) {
            URL url = null;
            try {
                // Try to parse the location as a URL...
                url = new URL(name);
            } catch (MalformedURLException ex) {
                return ImmutableList.of();
            }
            return ImmutableList.<Resource> of(new URLResource(url));
        }
    }
}