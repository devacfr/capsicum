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
import org.apache.cayenne.tx.TransactionManager;
import org.easymock.EasyMock;

public abstract class DomainTestHelper {

    public static CustomDataDomainDefinition domainOk() {
        return CustomDataDomainDefinition.builder()
                .name("domainok")
                .adapterClass(DerbyAdapter.class)
                .dataSource(EasyMock.createMock(DataSource.class))
                .location("class:cayenne-context.xml")
                .schemaUpdateStrategy(CreateIfNoSchemaStrategy.class)
                .transactionManager(EasyMock.createMock(TransactionManager.class))
                .build();
    }

    public static CustomDataDomainDefinition domainTestOk() {
        return CustomDataDomainDefinition.builder()
                .name("test")
                .adapterClass(DerbyAdapter.class)
                .dataSource(EasyMock.createMock(DataSource.class))
                .location("class:cayenne-context.xml")
                .schemaUpdateStrategy(CreateIfNoSchemaStrategy.class)
                .transactionManager(EasyMock.createMock(TransactionManager.class))
                .build();
    }
}
