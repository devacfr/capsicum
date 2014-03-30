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
package org.cfr.capsicum.spring.support;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SelectQuery;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.support.SelectQueryResponse;
import org.cfr.commons.util.Assert;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * A template for Cayenne access from DAO. Wraps Cayenne and SQLExceptions in
 * Spring DAO exception. Uses DataContext for all operations.
 * @author devacfr
 * @since 1.0
 */
public class CayenneTemplate extends CayenneAccessor implements ICayenneOperations {

    /**
     * Create new instance.
     * @param cayenneRuntimeFactory
     */
    public CayenneTemplate(@Nonnull final ICayenneRuntimeContext cayenneRuntimeFactory) {
        super(cayenneRuntimeFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitChanges() {
        ObjectContext context = getObjectContext();
        context.commitChanges();

    }

    /**
     * Creates a new persistent object of a given class scheduled to be inserted to the
     * database on next commit.
     * @param <U>
     * @param clazz
     * @return
     */
    public <U> U createInstance(Class<U> clazz) {
        return getObjectContext().newObject(clazz);
    }

    /**
     * Main worker method that wraps CayenneCalbac execution in Spring exception
     * handler.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> R execute(@Nonnull ICayenneCallback callback) throws DataAccessException {
        ObjectContext context = getObjectContext();
        try {
            R result = (R) callback.doInCayenne(context);
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                context.commitChanges();
            }
            return result;
        } catch (CayenneRuntimeException ex) {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                rollbackDataContext(context, ex);
            }
            throw convertAccessException(ex);
        } catch (CayenneException ex) {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                rollbackDataContext(context, ex);
            }
            throw convertAccessException(ex);
        }
    }

    /**
     *
     * @param clazz
     * @return
     * @throws DataAccessException
     */
    public <R> List<R> find(final Class<R> clazz) throws DataAccessException {
        ObjectContext context = getObjectContext();
        SelectQuery<R> query = new SelectQuery<R>(clazz);
        return context.select(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> R getObjectForQuery(final Query query) throws DataAccessException {
        ObjectContext context = getObjectContext();
        return (R) Cayenne.objectForQuery(context, query);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R objectForPK(final Class<R> dataObjectClass, final Object pk) throws DataAccessException {
        ObjectContext context = getObjectContext();
        return Cayenne.objectForPK(context, dataObjectClass, pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResponse performQuery(final Query query) {
        ObjectContext context = getObjectContext();
        return context.performGenericQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> List<R> performSelectQuery(final Query query) throws DataAccessException {
        ObjectContext context = getObjectContext();
        return context.performQuery(query);
    }

    @Override
    public <T> SelectQueryResponse<T> selectQuery(Class<T> dataObjectClass, SelectQuery<T> query)
            throws DataAccessException {
        return SelectQueryResponse.createResponse(getCayenneRuntime(), getObjectContext(), dataObjectClass, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollbackChanges() throws DataAccessException {
        // no Cayenne exceptions are thrown here...
        getObjectContext().rollbackChanges();
    }

    /**
     *
     * @param context
     * @param th
     */
    protected void rollbackDataContext(ObjectContext context, Throwable th) {
        context.rollbackChanges();
    }

    public void registerNewObject(Object object) {
        if (object instanceof CayenneDataObject
                && ((CayenneDataObject) object).getObjectContext() == null) {
            getObjectContext().registerNewObject(object);
        }
    }

    /**
     *
     * @param pc
     */
    public void delete(Object object) {
        if (object instanceof CayenneDataObject) {
            ObjectContext context = ((CayenneDataObject) object).getObjectContext();
            Assert.notNull(context, "object is not persistent");
            context.deleteObjects(object);
        }

    }

}