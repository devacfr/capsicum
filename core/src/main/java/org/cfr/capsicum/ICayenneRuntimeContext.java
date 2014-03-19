package org.cfr.capsicum;

import java.sql.SQLException;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.server.DataSourceFactory;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.capsicum.datasource.CayenneTransactionManager;
import org.cfr.capsicum.datasource.IDataSourceOperations;


/**
 * 
 * @author devacfr
 * @since 1.0
 */
public interface ICayenneRuntimeContext extends DataSourceFactory, IDataSourceOperations {

    /**
     * 
     * @return
     */
    ObjectContext createObjectContext();

    /**
     * 
     * @return
     */
    boolean isServerMode();

    /**
     * 
     * @return
     */
    List<DataDomainDefinition> getDataDomainDefinitions();

    /**
     * 
     * @return
     */
    DataDomain getDataDomain();

    /**
     * @throws SQLException 
     * 
     */
    void updateDatabaseSchema() throws SQLException;

    /**
     * Gets instance from cayenne DI according to binding declaration.
     * @param classType Class type of instance 
     * @return Returns instance from cayenne DI according to bind declaration
     */
    <T> T getInstance(Class<T> classType);

    /**
     * 
     * @return
     */
    CayenneTransactionManager getTransactionManager();

}