package org.cfr.capsicum.access;

import javax.sql.DataSource;

import org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy;
import org.apache.cayenne.dba.derby.DerbyAdapter;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.easymock.EasyMock;
import org.springframework.core.io.Resource;
import org.springframework.transaction.support.ResourceTransactionManager;


public abstract class DomainTestHelper {

    public static DataDomainDefinition domainOk() {
        DataDomainDefinition domainDefinition = new DataDomainDefinition("domainok");
        domainDefinition.setAdapterClass(DerbyAdapter.class);
        domainDefinition.setDataSource(EasyMock.createMock(DataSource.class));
        domainDefinition.setDomainResource(EasyMock.createMock(Resource.class));
        domainDefinition.setSchemaUpdateStrategy(CreateIfNoSchemaStrategy.class);
        domainDefinition.setTransactionManager(EasyMock.createMock(ResourceTransactionManager.class));
        return domainDefinition;
    }

    public static DataDomainDefinition domainTestOk() {
        DataDomainDefinition domainDefinition = new DataDomainDefinition("test");
        domainDefinition.setAdapterClass(DerbyAdapter.class);
        domainDefinition.setDataSource(EasyMock.createMock(DataSource.class));
        domainDefinition.setDomainResource(EasyMock.createMock(Resource.class));
        domainDefinition.setSchemaUpdateStrategy(CreateIfNoSchemaStrategy.class);
        domainDefinition.setTransactionManager(EasyMock.createMock(ResourceTransactionManager.class));
        return domainDefinition;
    }
}
