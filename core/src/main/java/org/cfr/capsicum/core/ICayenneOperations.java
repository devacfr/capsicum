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
package org.cfr.capsicum.core;

import java.util.List;

import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.dao.DataAccessException;

/**
 * A facade to Cayenne DataContext, adapting it for use with Spring DAO
 * exceptions, etc.
 */
public interface ICayenneOperations {

    /**
     * Flushes all changes to objects in this context to the parent DataChannel, cascading
     * flush operation all the way through the stack, ultimately saving data in the
     * database.
     * @throws DataAccessException
     */
    public void commitChanges() throws DataAccessException;

    /**
     * Runs an arbitrary DB operation defined as CayenneCallback.
     */
    public <R> R execute(ICayenneCallback callback) throws DataAccessException;

    /**
     * 
     * @param query
     * @return
     * @throws DataAccessException
     */
    public <T> T getObjectForQuery(final Query query) throws DataAccessException;

    /**
     * 
     * @param <T>
     * @param dataObjectClass
     * @param pk
     * @return
     * @throws DataAccessException
     */
    public <T> T objectForPK(Class<T> dataObjectClass, Object pk) throws DataAccessException;

    /**
     * 
     * @param query
     * @return
     * @throws DataAccessException
     */
    public QueryResponse performQuery(final Query query) throws DataAccessException;

    /**
     * 
     * @param query
     * @return
     * @throws DataAccessException
     */
    public <T> List<T> performSelectQuery(Query query) throws DataAccessException;

    /**
     * 
     * @param dataObjectClass
     * @param query
     * @return
     * @throws DataAccessException
     */
    public <T> SelectQueryResponse<T> selectQuery(Class<T> dataObjectClass, SelectQuery<T> query)
            throws DataAccessException;

    /**
     * 
     * @throws DataAccessException
     */
    public void rollbackChanges() throws DataAccessException;
}