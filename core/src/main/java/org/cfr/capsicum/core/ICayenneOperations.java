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
    public <T> SelectQueryResponse<T> selectQuery(Class<T> dataObjectClass, SelectQuery<T> query) throws DataAccessException;

    /**
     * 
     * @throws DataAccessException
     */
    public void rollbackChanges() throws DataAccessException;
}