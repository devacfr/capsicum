package org.cfr.capsicum.server;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.access.dbsync.SkipSchemaUpdateStrategy;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.DataChannelDescriptorLoader;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.XMLDataChannelDescriptorLoader;
import org.apache.cayenne.configuration.server.DataSourceFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.configuration.web.RequestHandler;
import org.apache.cayenne.configuration.web.StatelessContextRequestHandler;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.apache.cayenne.log.JdbcEventLogger;
import org.apache.cayenne.resource.ResourceLocator;
import org.apache.cayenne.tx.TransactionManager;
import org.apache.cayenne.tx.TransactionalOperation;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.capsicum.configuration.DataDomainProvider;
import org.cfr.capsicum.configuration.DecoratorDataChannelDescriptorLoader;
import org.cfr.capsicum.datasource.CayenneTransactionManager;
import org.cfr.capsicum.datasource.IDataSourceOperations;
import org.cfr.capsicum.datasource.SpringDataSourceFactory;
import org.cfr.capsicum.resource.SpringResourceLocator;
import org.cfr.commons.util.Assert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A singleton factory for Cayenne runtime.
 * @author devacfr
 * @since 1.0
 */
public class ServerRuntimeFactoryBean implements FactoryBean<CayenneRuntime>, ApplicationContextAware,
        InitializingBean, DisposableBean, ICayenneRuntimeContext, TransactionManager, IDataSourceOperations {

    protected ServerRuntime cayenneRuntime;

    private ApplicationContext applicationContext;

    private ResourceLoader resourceLoader;

    private List<DataDomainDefinition> dataDomainDefinitions;

    private Class<SchemaUpdateStrategy> defaultSchemaUpdateStrategy;

    private CayenneTransactionManager transactionManager;

    private DataSource dataSource;

    private DataSourceFactory dataSourceFactory;

    private boolean useSessionPersistentState = true;

    /**
     * 
     */
    public ServerRuntimeFactoryBean() {
        //noop
    }

    /**
     * 
     * @param dataSource
     */
    public ServerRuntimeFactoryBean(@Nonnull final DataSource dataSource) {
        this.dataSource = Assert.notNull(dataSource, "datasource is required");
    }

    /**
     * 
     * @param transactionManager
     */
    public ServerRuntimeFactoryBean(@Nonnull final CayenneTransactionManager transactionManager) {
        this.transactionManager = Assert.notNull(transactionManager, "transactionManager is required");
    }

    /**
     * Builds Cayenne configuration object based on configured properties.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // check datasource and transaction
        if (transactionManager == null && dataSource == null) {
            throw new IllegalArgumentException("Property 'transactionManager' or 'dataSource' is required");
        }

        // create default resource loader if no attached spring context.
        if (applicationContext == null) {
            this.resourceLoader = new DefaultResourceLoader();
        } else {
            this.resourceLoader = applicationContext;
        }
        SpringServerModule springModule = new SpringServerModule();
        List<String> configurationLocations = ImmutableList.of();
        if (dataDomainDefinitions != null && !dataDomainDefinitions.isEmpty()) {
            configurationLocations = Lists.newArrayListWithCapacity(dataDomainDefinitions.size());
            for (DataDomainDefinition dataDomainDefinition : dataDomainDefinitions) {
                Assert.notNull(dataDomainDefinition.getName(), "the name of DataDomain is required");
                Resource resource = Assert.notNull(dataDomainDefinition.getDomainResource(),
                    "the resource of DataDomain is required");
                URL url = null;
                try {
                    url = resource.getURL();
                } catch (IOException ex) {
                    throw new CayenneException(ex.getMessage(), ex);
                }
                configurationLocations.add(url.toString());
                // set default update strategy
                if (dataDomainDefinition.getSchemaUpdateStrategy() == null) {
                    dataDomainDefinition.setSchemaUpdateStrategy(getDefaultSchemaUpdateStrategy());
                }
            }
        }
        // create specific datasource Factory
        if (dataSourceFactory == null)
            dataSourceFactory = new SpringDataSourceFactory(this);
        // create cayenne runtime instance with domain definitions
        cayenneRuntime = new ServerRuntime(Iterables.toArray(configurationLocations, String.class), springModule);

        // create default transaction manager
        if (transactionManager == null) {
            transactionManager = new CayenneTransactionManager(dataSource, cayenneRuntime);
        } else {
            this.transactionManager.setCayenneRuntime(cayenneRuntime);
        }

    }

    /**
     * Shuts down underlying Configuration.
     */
    @Override
    public void destroy() throws Exception {
        applicationContext = null;
        if (cayenneRuntime != null) {
            cayenneRuntime.shutdown();
        }
    }

    @Override
    public CayenneRuntime getObject() throws Exception {
        return this.cayenneRuntime;
    }

    @Override
    public Class<?> getObjectType() {
        return CayenneRuntime.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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
    public <T> T getInstance(Class<T> classType) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource(@Nullable DataNodeDescriptor nodeDescriptor) throws Exception {
        DataSource dataSource = dataSourceFactory.getDataSource(nodeDescriptor);
        // if datasource is null, use default dataSource
        if (dataSource == null) {
            dataSource = this.getDataSource();
        }
        return dataSource;
    }

    /**
     * 
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 
     * @return
     */
    public DataSource getDataSource() {
        Object resource = transactionManager.getResourceFactory();
        if (resource instanceof DataSource) {
            return (DataSource) resource;
        }
        return dataSource;
    }

    @Override
    public void updateDatabaseSchema() throws SQLException {
        DataDomain domain = getDataDomain();
        if (domain == null)
            return;
        Collection<DataNode> dataNodes = domain.getDataNodes();
        for (DataNode dataNode : dataNodes) {
            SchemaUpdateStrategy strategy = dataNode.getSchemaUpdateStrategy();
            strategy.updateSchema(dataNode);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T performInTransaction(final TransactionalOperation<T> op) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<T>() {

            @Override
            public T doInTransaction(TransactionStatus status) {
                return op.perform();
            }
        });
    }

    @Override
    public <T> T
            execute(final @Nonnull TransactionDefinition transactionDefinition, final TransactionCallback<T> action)
                    throws TransactionException {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager,
                Assert.notNull(transactionDefinition, "transactionDefinition is required"));
        return transactionTemplate.execute(action);
    }

    /**
     * 
     * @param transactionManager
     */
    public void setTransactionManager(CayenneTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 
     * @return
     */
    @Override
    public CayenneTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public JdbcEventLogger getJdbcEventLogger() {
        return getInstance(JdbcEventLogger.class);
    }

    /**
     * 
     * @return
     */
    @Override
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
        return (defaultSchemaUpdateStrategy != null ? defaultSchemaUpdateStrategy : SkipSchemaUpdateStrategy.class);
    }

    /**
     * 
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    private class SpringServerModule implements Module {

        @Override
        public void configure(@Nonnull Binder binder) {
            // replace Default DataDomain Provider
            binder.bind(DataDomain.class).toProvider(DataDomainProvider.class);

            //todo [devacfr] replace toInstance by to, i.e. using spring injection
            binder.bind(DataSourceFactory.class).toInstance(ServerRuntimeFactoryBean.this);
            if (getDefaultSchemaUpdateStrategy() != null) {
                binder.bind(SchemaUpdateStrategy.class).to(getDefaultSchemaUpdateStrategy());
            }
            binder.bind(Key.get(DataChannelDescriptorLoader.class, "DefaultXMLDataChannelDescriptorLoader"))
                    .to(XMLDataChannelDescriptorLoader.class);
            binder.bind(DataChannelDescriptorLoader.class).toInstance(new DecoratorDataChannelDescriptorLoader(
                    ServerRuntimeFactoryBean.this));
            ResourceLocator resourceLocator = new SpringResourceLocator(getResourceLoader());
            binder.bind(ResourceLocator.class).toInstance(resourceLocator);
            binder.bind(TransactionManager.class).toInstance(ServerRuntimeFactoryBean.this);
            binder.bind(IDataSourceOperations.class).toInstance(ServerRuntimeFactoryBean.this);

            if (isUseSessionPersistentState()) {
                binder.bind(RequestHandler.class).to(SessionContextRequestHandler.class).withoutScope();
            } else {
                binder.bind(RequestHandler.class).to(StatelessContextRequestHandler.class).withoutScope();
            }
        }
    }
}