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
package org.cfr.capsicum.propertyset;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.dba.DbAdapter;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.CustomDataDomainDefinition;
import org.cfr.capsicum.access.DataDomainDefinition;
import org.cfr.capsicum.spring.server.ServerRuntimeFactoryBean;
import org.cfr.commons.util.Assert;
import org.cfr.commons.util.ClassLoaderUtils;

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
        if (this.cayenneRuntimeContext != null) {
            throw new RuntimeException("CayenneRuntimeContextProvider is already configured.");
        }
        ServerRuntimeFactoryBean factory = new ServerRuntimeFactoryBean();

        Iterator<String> itr = configurationProperties.keySet().iterator();
        while (itr.hasNext()) {
            CustomDataDomainDefinition.Builder dataDomainDefinition = CustomDataDomainDefinition.builder()
                    .location("classpath:cayenne-propertyset.xml")
                    .name(DOMAIN_NAME);
            String key = itr.next();

            if (key.startsWith(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY)) {
                dataDomainDefinition.dataSource((DataSource) configurationProperties.get(key));
                factory.setDataSource((DataSource) configurationProperties.get(key));
            }
            if (key.startsWith(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY)) {
                try {
                    dataDomainDefinition.adapterClass((Class<? extends DbAdapter>) ClassLoaderUtils.loadClass((String) configurationProperties.get(key),
                        this.getClass()));
                } catch (ClassNotFoundException e) {
                    throw new CayenneRuntimeException(e);
                }
            }
            if (key.startsWith(ICayenneConfigurationProvider.SCHEMA_UPDATE_STRATEGY_PROPERTY_KEY)) {
                dataDomainDefinition.schemaUpdateStrategy((Class<? extends SchemaUpdateStrategy>) configurationProperties.get(key));
            }
            factory.setDataDomainDefinitions(Lists.<DataDomainDefinition> newArrayList(dataDomainDefinition.buidl()));
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
