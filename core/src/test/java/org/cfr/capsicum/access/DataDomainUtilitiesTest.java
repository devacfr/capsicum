package org.cfr.capsicum.access;

import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.DataDomainUtilities;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.commons.testing.EasyMockTestCase;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class DataDomainUtilitiesTest extends EasyMockTestCase {

    @Test(expected = IllegalStateException.class)
    public void copyDataDomainDataMapWithEmptyDataDomain() {
        DataDomain sourceDataDomain = new DataDomain("source");
        DataDomain targetDataDomain = new DataDomain("destination");
        DataDomainUtilities.copyDataDomainDataMap(sourceDataDomain, targetDataDomain);
    }

    @Test(expected = IllegalStateException.class)
    public void copyDataDomainDataMap() {
        DataDomain sourceDataDomain = new DataDomain("source");
        sourceDataDomain.addDataMap(new DataMap("default"));
        sourceDataDomain.addDataMap(new DataMap("client"));
        DataDomain targetDataDomain = new DataDomain("destination");
        targetDataDomain.addDataMap(new DataMap("default"));
        sourceDataDomain.addDataMap(new DataMap("provider"));
        DataDomainUtilities.copyDataDomainDataMap(sourceDataDomain, targetDataDomain);
    }

    @Test
    public void findDataDomainDefinitionWithDomainName() {
        ICayenneRuntimeContext cayenneRuntimeContext = mock(ICayenneRuntimeContext.class);
        DataDomainDefinition domainDefinition = DomainTestHelper.domainOk();
        expect(cayenneRuntimeContext.getDataDomainDefinitions()).andReturn(ImmutableList.of(domainDefinition));
        replay();
        assertEquals(domainDefinition,
            DataDomainUtilities.findDataDomainDefinition(cayenneRuntimeContext, domainDefinition.getName()));
        verify();
    }

    @Test
    public void findDataDomainDefinitionWithDataNodeDescriptor() {
        ICayenneRuntimeContext cayenneRuntimeContext = mock(ICayenneRuntimeContext.class);
        DataDomainDefinition domainDefinition = DomainTestHelper.domainOk();
        expect(cayenneRuntimeContext.getDataDomainDefinitions()).andReturn(ImmutableList.of(domainDefinition));
        DataNodeDescriptor nodeDescriptor = new DataNodeDescriptor("DomainOk");
        DataChannelDescriptor dataChannelDescriptor = new DataChannelDescriptor();
        dataChannelDescriptor.setName(domainDefinition.getName());
        nodeDescriptor.setDataChannelDescriptor(dataChannelDescriptor);
        replay();
        assertEquals(domainDefinition,
            DataDomainUtilities.findDataDomainDefinition(cayenneRuntimeContext, nodeDescriptor));
        verify();
    }

}
