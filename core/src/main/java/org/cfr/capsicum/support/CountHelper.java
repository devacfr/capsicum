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
package org.cfr.capsicum.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.trans.SelectTranslator;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.SelectQuery;
import org.cfr.capsicum.ICayenneRuntimeContext;

/**
 *
 * @author devacfr
 * @since 1.0
 */
public class CountHelper {

    public static <T> long count(ObjectContext context,
                                 ICayenneRuntimeContext cayenneRuntimeContext,
                                 Class<T> objectClass,
                                 SelectQuery<T> query) {
        return count(context, cayenneRuntimeContext.getDataDomain(), objectClass, query);
    }

    public static <T> long count(ObjectContext context,
                                 DataDomain dataDomain,
                                 Class<T> objectClass,
                                 SelectQuery<T> query) {
        CountTranslator translator = new CountTranslator();

        ObjEntity objEntity = dataDomain.getEntityResolver().getObjEntity(objectClass);
        DbEntity entity = objEntity.getDbEntity();

        if (entity == null) {
            throw new CayenneRuntimeException("No entity is mapped for java class: "
                    + objectClass.getName());
        }
        DataNode node = dataDomain.lookupDataNode(entity.getDataMap());

        translator.setQuery(query);
        translator.setAdapter(node.getAdapter());
        translator.setEntityResolver(context.getEntityResolver());

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = node.getDataSource().getConnection();
            translator.setConnection(con);

            stmt = translator.createStatement();

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }

            throw new Exception("Count query returned no result");
        } catch (Exception e) {
            throw new RuntimeException("Cannot count", e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                throw new RuntimeException("Cannot close connection", ex);
            }
        }
    }

    static class CountTranslator extends SelectTranslator {

        @Override
        public String createSqlString() throws Exception {
            String sql = super.createSqlString();
            int index = sql.indexOf(" FROM ");
            sql = sql.substring(index);
            int ordering = sql.indexOf(" ORDER BY ");
            if (ordering > 0) {
                sql = sql.substring(0, ordering);
            }
            return "SELECT COUNT(*)"
            + sql;
        }
    }

}