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
package org.cfr.capsicum;

import java.sql.SQLException;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.server.DataSourceFactory;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.capsicum.datasource.CayenneTransactionManager;
import org.cfr.capsicum.datasource.IDataSourceOperations;

/**
 * 
 * @author devacfr
 * @since 1.0
 */
public interface ICayenneRuntimeContext extends DataSourceFactory, IDataSourceOperations {

    /**
     * 
     * @return
     */
    ObjectContext createObjectContext();

    /**
     * 
     * @return
     */
    boolean isServerMode();

    /**
     * 
     * @return
     */
    List<DataDomainDefinition> getDataDomainDefinitions();

    /**
     * 
     * @return
     */
    DataDomain getDataDomain();

    /**
     * @throws SQLException 
     * 
     */
    void updateDatabaseSchema() throws SQLException;

    /**
     * Gets instance from cayenne DI according to binding declaration.
     * @param classType Class type of instance 
     * @return Returns instance from cayenne DI according to bind declaration
     */
    <T> T getInstance(Class<T> classType);

    /**
     * 
     * @return
     */
    CayenneTransactionManager getTransactionManager();

}