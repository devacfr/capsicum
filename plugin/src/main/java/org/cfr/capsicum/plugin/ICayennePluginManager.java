package org.cfr.capsicum.plugin;

import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.plugin.descriptors.CayenneModuleDescriptor;


public interface ICayennePluginManager {

    void mergeDataDomain(ICayenneRuntimeContext cayenneRuntimeContext) throws Exception;

    void addCayenneModule(CayenneModuleDescriptor cayenneModuleDescriptor);

    void removeCayenneModule(CayenneModuleDescriptor cayenneModuleDescriptor);

}