package org.cfr.capsicum.datasource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.server.DataSourceFactory;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.DataDomainUtilities;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.commons.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;


public class SpringDataSourceFactory implements DataSourceFactory {

    private final ICayenneRuntimeContext cayenneRuntimeContext;

    @Autowired
    public SpringDataSourceFactory(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext) {
        this.cayenneRuntimeContext = Assert.notNull(cayenneRuntimeContext, "cayenneRuntimeContext is required");
    }

    @Override
    public DataSource getDataSource(DataNodeDescriptor nodeDescriptor) throws Exception {
        DataDomainDefinition dataDomain = DataDomainUtilities.findDataDomainDefinition(cayenneRuntimeContext,
            nodeDescriptor);
        if (dataDomain == null)
            return null;
        return dataDomain.getDataSource();
    }
}
