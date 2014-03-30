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

import java.util.Collection;
import java.util.Collections;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.cfr.capsicum.ICayenneRuntimeContext;

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

        return new SelectQueryResponse<T>(context.performQuery(query), CountHelper.count(context,
            cayenneContext.getDataDomain(),
            classObject,
            query));
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