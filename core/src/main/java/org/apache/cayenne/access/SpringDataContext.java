package org.apache.cayenne.access;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ResultIterator;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.Select;

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
     * 
     * @since 1.2
     */
    public SpringDataContext(DataChannel channel, ObjectStore objectStore) {
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
     * {@link #iterate(Select, org.apache.cayenne.ResultIteratorCallback)} to
     * get access to objects.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ResultIterator performIteratedQuery(Query query) {
        // [devacfr] not need special transaction
        return internalPerformIteratedQuery(query);

    }
}
