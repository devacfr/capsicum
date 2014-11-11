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

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.cayenne.ConfigurationException;
import org.apache.cayenne.access.TransactionalDataDomain;
import org.apache.cayenne.configuration.ConfigurationTree;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataChannelDescriptorLoader;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.resource.Resource;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.DataDomainDefinition;
import org.cfr.capsicum.access.DataDomainUtilities;
import org.cfr.commons.util.Assert;

/**
 * This class allows override  values of {@link TransactionalDataDomain} configured in xml file.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 * @see DataChannelDescriptorLoader
 */
public class DecoratorDataChannelDescriptorLoader implements DataChannelDescriptorLoader {

    /**
     *
     */
    private final ICayenneRuntimeContext cayenneRuntimeContext;

    /**
     *
     */
    @Inject("DefaultXMLDataChannelDescriptorLoader")
    protected DataChannelDescriptorLoader delegate;

    /**
     *
     * @param cayenneRuntimeContext Cayenne runtime context.
     * @param delegate delegate allowing to load configuration.
     */
    public DecoratorDataChannelDescriptorLoader(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext,
            @Nonnull final DataChannelDescriptorLoader delegate) {
        this.cayenneRuntimeContext = Assert.notNull(cayenneRuntimeContext);
        this.delegate = Assert.notNull(delegate);
    }

    /**
     *
     * @param cayenneRuntimeContext Cayenne runtime context.
     */
    public DecoratorDataChannelDescriptorLoader(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext) {
        this.cayenneRuntimeContext = Assert.notNull(cayenneRuntimeContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationTree<DataChannelDescriptor> load(@Nonnull final Resource configurationResource)
            throws ConfigurationException {
        Assert.notNull(delegate, "DataChannelDescriptor delegate loader is required");
        ConfigurationTree<DataChannelDescriptor> descriptor = delegate.load(configurationResource);

        DataDomainDefinition dataDomainDefinition = DataDomainUtilities.findDataDomainDefinition(cayenneRuntimeContext,
            descriptor.getRootNode().getName());
        if (dataDomainDefinition != null) {

            Collection<DataNodeDescriptor> dataNodeDescriptors = descriptor.getRootNode().getNodeDescriptors();
            for (DataNodeDescriptor dataNodeDescriptor : dataNodeDescriptors) {
                if (configurationResource.equals(dataNodeDescriptor.getConfigurationSource())) {
                    dataDomainDefinition.configure(cayenneRuntimeContext, dataNodeDescriptor);
                }
            }
        } else {
            if (configurationResource != null) {
                throw new ConfigurationException("Do not exist DataDomainDefinition for this resource '"
                        + configurationResource.toString() + "'");
            }
        }
        return descriptor;
    }
}
