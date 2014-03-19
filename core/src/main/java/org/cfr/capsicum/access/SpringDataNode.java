package org.cfr.capsicum.access;

import java.sql.SQLException;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.OperationObserver;
import org.apache.cayenne.query.Query;
import org.cfr.capsicum.datasource.TransactionAwareDataSourceProxy;


/**
 * 
 * @author devacfr
 * @since 1.0
 */
public class SpringDataNode extends DataNode {

    /**
     * Create new default instance.
     */
    public SpringDataNode() {
        super();
    }

    /**
     * 
     * @param name the datanode name
     */
    public SpringDataNode(final String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = createTransactionAwareDataSource(dataSource);
    }

    /**
     * 
     * @param ds datasource
     * @return Returns new Datasource if necessary, adding awareness of Spring-managed transactions.
     */
    protected DataSource createTransactionAwareDataSource(@Nullable final DataSource ds) {
        if (ds != null) {
            return new TransactionAwareDataSourceProxy(ds);
        }
        return ds;
    }

    @Override
    public void performQueries(final Collection<? extends Query> queries, final OperationObserver callback) {
        if (schemaUpdateStrategy != null) {
            try {
                schemaUpdateStrategy.updateSchema(this);
            } catch (SQLException e) {
                new CayenneRuntimeException(e);
            }
        }
        super.performQueries(queries, callback);
    }
}
