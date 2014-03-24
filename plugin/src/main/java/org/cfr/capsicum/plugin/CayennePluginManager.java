package org.cfr.capsicum.plugin;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.plugin.descriptors.CayenneModuleDescriptor;
import org.springframework.stereotype.Component;


@Component
public class CayennePluginManager implements ICayennePluginManager {

    private final Map<String, CayenneModuleDescriptor> cayenneDescriptors = new HashMap<String, CayenneModuleDescriptor>();

    @Override
    public void mergeDataDomain(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext) throws Exception {
        //        for (final CayenneModuleDescriptor cayenneModuleDescriptor : this.cayenneDescriptors.values()) {
        //            MultiProjectConfiguration conf = new MultiProjectConfiguration() {
        //
        //                @Override
        //                public void initialize() throws Exception {
        //                    setDomainConfigurationName(cayenneModuleDescriptor.getDomainFilename());
        //                    super.initialize();
        //                }
        //            };
        //            conf.initialize();
        //            DataDomainUtilities.copyDataDomainDataMap(conf.getDomain(), configuration.getDomain());
        //        }
        //TODO find solution
    }

    @Override
    public void addCayenneModule(@Nonnull final CayenneModuleDescriptor cayenneModuleDescriptor) {
        cayenneDescriptors.put(cayenneModuleDescriptor.getCompleteKey(), cayenneModuleDescriptor);

    }

    @Override
    public void removeCayenneModule(@Nonnull final CayenneModuleDescriptor cayenneModuleDescriptor) {
        cayenneDescriptors.remove(cayenneModuleDescriptor.getCompleteKey());
    }

}
