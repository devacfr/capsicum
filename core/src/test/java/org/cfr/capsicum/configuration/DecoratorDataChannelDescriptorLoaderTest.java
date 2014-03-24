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

import java.io.FileNotFoundException;

import org.apache.cayenne.ConfigurationException;
import org.apache.cayenne.configuration.ConfigurationTree;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataChannelDescriptorLoader;
import org.apache.cayenne.configuration.DefaultConfigurationNameMapper;
import org.apache.cayenne.configuration.XMLDataChannelDescriptorLoader;
import org.apache.cayenne.configuration.XMLDataMapLoader;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.URLResource;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.DomainTestHelper;
import org.cfr.commons.testing.EasyMockTestCase;
import org.cfr.commons.util.ResourceUtils;
import org.cfr.commons.util.collection.CollectionBuilder;
import org.junit.Test;

public class DecoratorDataChannelDescriptorLoaderTest extends EasyMockTestCase {

    @Test
    public void load() throws FileNotFoundException {
        Resource resource = new URLResource(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + getPackageName()
                + "/cayenne-test.xml"));
        ICayenneRuntimeContext cayenneRuntimeContext = mock(ICayenneRuntimeContext.class);
        expect(cayenneRuntimeContext.getDataDomainDefinitions()).andReturn(CollectionBuilder.list((DomainTestHelper.domainTestOk())));

        DataChannelDescriptorLoader delegate = new XMLDataChannelDescriptorLoader() {

            @Override
            public ConfigurationTree<DataChannelDescriptor> load(Resource configurationResource)
                    throws ConfigurationException {
                dataMapLoader = new XMLDataMapLoader();
                nameMapper = new DefaultConfigurationNameMapper();
                return super.load(configurationResource);
            }
        };
        DecoratorDataChannelDescriptorLoader loader = new DecoratorDataChannelDescriptorLoader(cayenneRuntimeContext,
                delegate);

        replay();

        loader.load(resource);
        verify();
    }

    @Test(expected = ConfigurationException.class)
    public void loadWrongDomain() throws FileNotFoundException {
        Resource resource = new URLResource(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + getPackageName()
                + "/cayenne-test.xml"));
        ICayenneRuntimeContext cayenneRuntimeContext = mock(ICayenneRuntimeContext.class);
        // Give a wrong domain, ie wrong name
        expect(cayenneRuntimeContext.getDataDomainDefinitions()).andReturn(CollectionBuilder.list((DomainTestHelper.domainOk())))
                .once();

        DataChannelDescriptorLoader delegate = new XMLDataChannelDescriptorLoader() {

            @Override
            public ConfigurationTree<DataChannelDescriptor> load(Resource configurationResource)
                    throws ConfigurationException {
                dataMapLoader = new XMLDataMapLoader();
                nameMapper = new DefaultConfigurationNameMapper();
                return super.load(configurationResource);
            }
        };
        DecoratorDataChannelDescriptorLoader loader = new DecoratorDataChannelDescriptorLoader(cayenneRuntimeContext,
                delegate);

        replay();

        loader.load(resource);
        verify();
    }

    @Test(expected = FileNotFoundException.class)
    public void loadWrongDomainFile() throws Throwable {
        Resource resource = new URLResource(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + getPackageName()
                + "/cayenne-wrong.xml"));
        ICayenneRuntimeContext cayenneRuntimeContext = mock(ICayenneRuntimeContext.class);
        // Give a wrong domain, ie wrong name
        expect(cayenneRuntimeContext.getDataDomainDefinitions()).andReturn(CollectionBuilder.list((DomainTestHelper.domainOk())))
                .once();

        DataChannelDescriptorLoader delegate = new XMLDataChannelDescriptorLoader() {

            @Override
            public ConfigurationTree<DataChannelDescriptor> load(Resource configurationResource)
                    throws ConfigurationException {
                dataMapLoader = new XMLDataMapLoader();
                nameMapper = new DefaultConfigurationNameMapper();
                return super.load(configurationResource);
            }
        };
        DecoratorDataChannelDescriptorLoader loader = new DecoratorDataChannelDescriptorLoader(cayenneRuntimeContext,
                delegate);

        replay();
        try {
            loader.load(resource);

        } catch (Throwable ex) {
            verify();
            throw ex;
        }
        fail();

    }
}
