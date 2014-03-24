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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.access.dbsync.BaseSchemaUpdateStrategy;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * https://issues.apache.org/jira/browse/CAY-1922 Bug fix default implementation
 * {@link org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy}
 * table name comparison between database and metadata. (Mysql database)
 * Replace
 * returns all dbentities from all DataMaps from all DataNode
 * <p>the Collection<DbEntity> entities = dataNode.getEntityResolver().getDbEntities();</p>
 * by
 * <p>Collection<DataMap> map = dataNode.getDataMaps();</p>
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class CreateIfNoSchemaStrategy extends BaseSchemaUpdateStrategy {

    /**
     * Static class logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(CreateIfNoSchemaStrategy.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void processSchemaUpdate(@Nonnull final DataNode dataNode) throws SQLException {

        Map<String, Boolean> nameTables = getNameTablesInDB(dataNode);
        Set<DbEntity> entities = Sets.newHashSet();
        Collection<DataMap> map = dataNode.getDataMaps();
        for (DataMap dataMap : map) {
            entities.addAll(dataMap.getDbEntities());
        }
        boolean generate = true;
        Iterator<DbEntity> it = entities.iterator();
        while (it.hasNext()) {
            if (nameTables.get(it.next().getName().toLowerCase()) != null) {
                generate = false;
                break;
            }
        }

        if (generate) {
            LOGGER.info("No schema detected, will create mapped tables");
            generate(dataNode);
        } else {
            LOGGER.info("Full or partial schema detected, skipping tables creation");
        }
    }

    /**
     * Generate sql script and execute.
     * @param dataNode a datanode
     */
    private void generate(@Nonnull final DataNode dataNode) {
        Collection<DataMap> map = dataNode.getDataMaps();
        Iterator<DataMap> iterator = map.iterator();
        while (iterator.hasNext()) {
            DbGenerator gen = new DbGenerator(dataNode.getAdapter(), iterator.next(), dataNode.getJdbcEventLogger());
            gen.setShouldCreateTables(true);
            gen.setShouldDropTables(false);
            gen.setShouldCreateFKConstraints(true);
            gen.setShouldCreatePKSupport(true);
            gen.setShouldDropPKSupport(false);
            try {
                gen.runGenerator(dataNode.getDataSource());
            } catch (Exception e) {
                throw new CayenneRuntimeException(e);
            }
        }
    }

    /**
     * Gets all the table names in database.
     * @param dataNode a datanode
     * @return Returns all the table names in database.
     * @throws SQLException Returns all the table names in database.
     */
    @Nonnull
    protected Map<String, Boolean> getNameTablesInDB(@Nonnull final DataNode dataNode) throws SQLException {
        String tableLabel = dataNode.getAdapter().tableTypeForTable();
        Connection con = null;
        Map<String, Boolean> nameTables = new HashMap<String, Boolean>();
        con = dataNode.getDataSource().getConnection();

        try {
            ResultSet rs = con.getMetaData().getTables(null, null, "%", new String[] { tableLabel });

            try {

                while (rs.next()) {
                    String name = rs.getString("TABLE_NAME");
                    nameTables.put(name.toLowerCase(), false);
                }
            } finally {
                rs.close();
            }

        } finally {

            con.close();
        }
        return nameTables;
    }
}