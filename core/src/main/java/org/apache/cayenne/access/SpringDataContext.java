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
package org.apache.cayenne.access;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ResultIterator;
import org.apache.cayenne.query.Query;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class SpringDataContext extends DataContext {

    /**
     * 
     */
    private static final long serialVersionUID = -6647366270086179134L;

    /**
     * Creates a new DataContext that is not attached to the Cayenne stack.
     */
    public SpringDataContext() {
        this(null, null);
    }

    /**
     * Creates a new DataContext with parent DataChannel and ObjectStore.
     * @param channel the data channel.
     * @param objectStore the object store.
     */
    public SpringDataContext(final DataChannel channel, final ObjectStore objectStore) {
        super(null, null);
    }

    /**
     * Performs a single database select query returning result as a
     * ResultIterator. It is caller's responsibility to explicitly close the
     * ResultIterator. A failure to do so will result in a database connection
     * not being released. Another side effect of an open ResultIterator is that
     * an internal Cayenne transaction that originated in this method stays open
     * until the iterator is closed. So users should normally close the iterator
     * within the same thread that opened it.
     * <p>
     * Note that 'performIteratedQuery' always returns ResultIterator over
     * DataRows. Use
     * {@link #iterate(org.apache.cayenne.query.Select, org.apache.cayenne.ResultIteratorCallback)} to
     * get access to objects.
     * @param query a query
     * @return Returns {@link ResultIterator}.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ResultIterator performIteratedQuery(final Query query) {
        //TODO [devacfr] not need special transaction
        return internalPerformIteratedQuery(query);

    }
}
