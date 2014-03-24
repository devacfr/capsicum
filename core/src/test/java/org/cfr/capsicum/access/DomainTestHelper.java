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
