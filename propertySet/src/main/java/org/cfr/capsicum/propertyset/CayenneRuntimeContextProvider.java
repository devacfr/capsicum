package org.cfr.capsicum.propertyset;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.dba.DbAdapter;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.capsicum.server.ServerRuntimeFactoryBean;
import org.cfr.commons.util.Assert;
import org.cfr.commons.util.ClassLoaderUtils;
import org.springframework.core.io.DefaultResourceLoader;

import com.google.common.collect.Lists;

public class CayenneRuntimeContextProvider implements ICayenneConfigurationProvider {

    private ICayennePropertySetDAO propertySetDAO;

    private ICayenneRuntimeContext cayenneRuntimeContext;

    public CayenneRuntimeContextProvider() {
    }

    public CayenneRuntimeContextProvider(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext) {
        this.cayenneRuntimeContext = Assert.notNull(cayenneRuntimeContext);
    }

    @Override
    public ICayennePropertySetDAO getPropertySetDAO() {
        if (propertySetDAO == null) {
            propertySetDAO = new CayennePropertySetDAOImpl(cayenneRuntimeContext);
        }

        return propertySetDAO;
    }

    public ICayenneRuntimeContext getCayenneRuntimeContext() {
        return cayenneRuntimeContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setup(Map<String, Object> configurationProperties) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        if (this.cayenneRuntimeContext != null) {
            throw new RuntimeException("CayenneRuntimeContextProvider is already configured.");
        }
        ServerRuntimeFactoryBean factory = new ServerRuntimeFactoryBean();
        DataDomainDefinition dataDomainDefinition = new DataDomainDefinition();
        dataDomainDefinition.setDomainResource(resourceLoader.getResource("classpath:cayenne-propertyset.xml"));
        factory.setDataDomainDefinitions(Lists.newArrayList(dataDomainDefinition));
        dataDomainDefinition.setName(DOMAIN_NAME);

        Iterator<String> itr = configurationProperties.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();

            if (key.startsWith(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY)) {
                dataDomainDefinition.setDataSource((DataSource) configurationProperties.get(key));
                factory.setDataSource((DataSource) configurationProperties.get(key));
            }
            if (key.startsWith(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY)) {
                try {
                    dataDomainDefinition.setAdapterClass((Class<? extends DbAdapter>) ClassLoaderUtils.loadClass((String) configurationProperties.get(key),
                        this.getClass()));
                } catch (ClassNotFoundException e) {
                    throw new CayenneRuntimeException(e);
                }
            }
            if (key.startsWith(ICayenneConfigurationProvider.SCHEMA_UPDATE_STRATEGY_PROPERTY_KEY)) {
                dataDomainDefinition.setSchemaUpdateStrategy((Class<? extends SchemaUpdateStrategy>) configurationProperties.get(key));
            }

        }
        try {
            factory.afterPropertiesSet();
            this.cayenneRuntimeContext = factory;

        } catch (Exception e) {
            ///CLOVER:OFF
            e.printStackTrace();
            ///CLOVER:ON
        }
    }
}
