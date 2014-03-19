package org.cfr.capsicum.access;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.commons.lang.StringUtils;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.commons.util.Assert;


/**
 * This abstract class contains utilities for working with Cayenne DataDomains.
 * @author devacfr
 * @since 1.0
 */
public abstract class DataDomainUtilities {

    /**
     * Copies a Cayenne DataDomain's DataMap from a source DataDomain to a target
     * DataDomain.  Both the source and target must already exist in the Cayenne
     * model.  This is useful when using the same map (schema is identical) across
     * different databases.  You can define a "shared" map and then copy it to each
     * DataDomain required (which has it's own connection information in the DataNode).
     * @param sourceDataDomain source of datadomain
     * @param targetDataDomain destination of datadomain
     */
    public static void copyDataDomainDataMap(@Nonnull final DataDomain sourceDataDomain,
                                             @Nonnull final DataDomain targetDataDomain) {

        Collection<DataMap> sourceDataMaps = sourceDataDomain.getDataMaps();
        Collection<DataMap> targetDataMaps = targetDataDomain.getDataMaps();

        Assert.state(sourceDataMaps.size() == 1, "sourceDataDomain have to only 1 dataMap");
        Assert.state(targetDataMaps.size() == 1, "targetDataDomain have to only 1 dataMap");

        DataMap target = targetDataMaps.iterator().next();
        DataMap source = sourceDataMaps.iterator().next();

        target.mergeWithDataMap(source);
    }

    /**
     * Finds the datadomain definition is the specified domain name paramter. 
     * @param domainName the domain name to find
     * @param cayenneRuntimeContext cayenne runtime context
     * @return Returns the datadomain definition is the specified domain name paramter, 
     * otherwise <code>null</code> 
     */
    public static DataDomainDefinition
            findDataDomainDefinition(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext,
                                     @Nullable final String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return null;
        }
        for (DataDomainDefinition dataDomainDefinition : cayenneRuntimeContext.getDataDomainDefinitions()) {
            String name = dataDomainDefinition.getName();
            if (domainName.equals(name)) {
                return dataDomainDefinition;
            }
        }
        return null;
    }

    /**
     * Finds the datadomain definition associated to dataNodeDescriptor paramter. 
     * @param dataNodeDescriptor the datanodedescriptor
     * @param cayenneRuntimeContext cayenne runtime context
     * @return Returns the datadomain definition associated to dataNodeDescriptor paramter,
     * otherwise <code>false</code>.
     */
    public static DataDomainDefinition
            findDataDomainDefinition(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext,
                                     @Nonnull final DataNodeDescriptor dataNodeDescriptor) {
        Assert.notNull(cayenneRuntimeContext, "cayenneRuntimeContext is required");
        Assert.notNull(dataNodeDescriptor, "dataNodeDescriptor is required");
        Assert.notNull(dataNodeDescriptor.getDataChannelDescriptor(),
            "dataNodeDescriptor.getDataChannelDescriptor() is required");
        return findDataDomainDefinition(cayenneRuntimeContext, dataNodeDescriptor.getDataChannelDescriptor().getName());
    }
}
