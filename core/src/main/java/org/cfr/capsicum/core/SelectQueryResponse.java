package org.cfr.capsicum.core;

import java.util.Collection;
import java.util.Collections;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.support.CountHelper;


/**
 * 
 * @author devacfr
 * @since 1.0
 * @param <T> T paremeter type must be a {@link CayenneDataObject}.
 */
public class SelectQueryResponse<T> {

    private static final SelectQueryResponse<?> EMPTY = new SelectQueryResponse<Object>(Collections.emptyList(), 0);

    private final Collection<T> datasource;

    private final long totalCount;

    /**
     * 
     * @param cayenneContext
     * @param context
     * @param classObject
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> SelectQueryResponse<T> createResponse(ICayenneRuntimeContext cayenneContext,
                                                            ObjectContext context,
                                                            Class<T> classObject,
                                                            SelectQuery<T> query) {

        return new SelectQueryResponse<T>(context.performQuery(query), CountHelper.count(context, cayenneContext.getDataDomain(), classObject, query));
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> SelectQueryResponse<T> empty() {
        return (SelectQueryResponse<T>) EMPTY;
    }

    /**
     * 
     * @param datasource
     * @param totalCount
     */
    public SelectQueryResponse(Collection<T> datasource, long totalCount) {
        this.datasource = datasource;
        this.totalCount = totalCount;
    }

    /**
     * 
     * @return
     */
    public long totalCount() {
        return totalCount;
    }

    /**
     * 
     * @return
     */
    public Collection<T> list() {
        return datasource;
    }
}