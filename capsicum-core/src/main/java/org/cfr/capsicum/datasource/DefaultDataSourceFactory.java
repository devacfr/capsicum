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
package org.cfr.capsicum.datasource;

import javax.sql.DataSource;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.server.DelegatingDataSourceFactory;
import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.Inject;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.access.CustomDataDomainDefinition;
import org.cfr.capsicum.access.DataDomainDefinition;
import org.cfr.capsicum.access.DataDomainUtilities;

public class DefaultDataSourceFactory extends DelegatingDataSourceFactory {

    @Inject
    private ICayenneRuntimeContext cayenneRuntimeContext;

    @Inject
    protected AdhocObjectFactory objectFactory;

    @Override
    public DataSource getDataSource(final DataNodeDescriptor nodeDescriptor) throws Exception {
        DataDomainDefinition dataDomain = DataDomainUtilities.findDataDomainDefinition(cayenneRuntimeContext,
            nodeDescriptor);
        if (dataDomain == null) {
            return null;
        }
        if (dataDomain instanceof CustomDataDomainDefinition) {
            return ((CustomDataDomainDefinition) dataDomain).getDataSource();
        }
        return null;
    }
}
