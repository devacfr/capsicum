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
package org.cfr.capsicum.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.cfr.commons.util.jdbc.access.AdapterFactory;
import org.cfr.commons.util.jdbc.access.IDbAdapter;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.CachedResultSetTableFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IResultSetTableFactory;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvProducer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatDtdProducer;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlDataSetWriter;
import org.dbunit.dataset.xml.XmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.xml.sax.InputSource;

public abstract class AbstractCayenneJUnit4DbUnitSpringContextTests extends AbstractCayenneJUnit4SpringContextTests {

    private String[] dataSetDBUnit = null;

    private ProducerType producerType = ProducerType.Xml;

    /**
     * The SimpleJdbcTemplate that this base class manages, available to
     * subclasses.
     */
    protected SimpleJdbcTemplate simpleJdbcTemplate;

    private String sqlScriptEncoding;

    private DataSource dataSource;

    private IDbAdapter adapter;

    @SuppressWarnings("unused")
    private AbstractCayenneJUnit4DbUnitSpringContextTests() {
    }

    protected AbstractCayenneJUnit4DbUnitSpringContextTests(ProducerType producerType, String... dataSetDBUnit) {
        this.dataSetDBUnit = dataSetDBUnit;
        this.producerType = producerType;
    }

    protected AbstractCayenneJUnit4DbUnitSpringContextTests(String... dataSetDBUnit) {
        this.dataSetDBUnit = dataSetDBUnit;
    }

    private void addTableName(ArrayList<String> tables, DbEntity dbEntity) {
        if (tables.contains(dbEntity.getFullyQualifiedName())) {
            return;
        }
        Collection<DbRelationship> relationships = dbEntity.getRelationships();
        if (relationships.size() > 0) {
            for (DbRelationship relationship : relationships) {
                if (relationship.isToMany()
                        && !dbEntity.equals(relationship.getTargetEntity())) {
                    addTableName(tables, (DbEntity) relationship.getTargetEntity());
                }
            }
        }
        tables.add(dbEntity.getFullyQualifiedName());
    }

    protected void backupDatabase(String charsetName, File file) throws SQLException, IOException,
            DatabaseUnitException {
        mkdir(file.getParent());
        // initialize your database connection here
        IDatabaseConnection connection = new DatabaseConnection(DataSourceUtils.getConnection(getDataSource()));
        // initialize your dataset here
        try {
            IDataSet dataSet = getDatabaseDataSet(connection, getTableNames(true), false);
            XmlDataSetWriter writer = new XmlDataSetWriter(new java.io.OutputStreamWriter(new FileOutputStream(file),
                    charsetName));
            writer.write(dataSet);
        } finally {
            DataSourceUtils.releaseConnection(connection.getConnection(), getDataSource());
        }
    }

    private static void mkdir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Count the rows in the given table.
     *
     * @param tableName
     *            table name to count rows in
     * @return the number of rows in the table
     */
    protected int countRowsInTable(String tableName) {
        return SimpleJdbcTestUtils.countRowsInTable(this.simpleJdbcTemplate, tableName);
    }

    /**
     * Convenience method for deleting all rows from the specified tables. Use
     * with caution outside of a transaction!
     *
     * @param names
     *            the names of the tables from which to delete
     * @return the total number of rows deleted from all specified tables
     */
    protected int deleteFromTables(String... names) {
        // FIXME execute all script before delete , delete and after delete in transaction
        int count = -1;
        try {
            if (adapter != null) {
                for (String stmt : adapter.unCheckForeignKeyStatements()) {
                    safeExecute(stmt);
                }
            }
            count = SimpleJdbcTestUtils.deleteFromTables(this.simpleJdbcTemplate, names);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            if (adapter != null) {
                for (String stmt : adapter.checkForeignKeyStatements()) {
                    try {
                        safeExecute(stmt);
                    } catch (SQLException e) {
                        logger.error(e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * Execute the given SQL script. Use with caution outside of a transaction!
     * <p>
     * The script will normally be loaded by classpath. There should be one
     * statement per line. Any semicolons will be removed. <b>Do not use this
     * method to execute DDL if you expect rollback.</b>
     *
     * @param sqlResourcePath
     *            the Spring resource path for the SQL script
     * @param continueOnError
     *            whether or not to continue without throwing an exception in
     *            the event of an error
     * @throws DataAccessException
     *             if there is an error executing a statement and
     *             continueOnError was <code>false</code>
     */
    protected void executeSqlScript(String sqlResourcePath, boolean continueOnError) throws DataAccessException {

        Resource resource = this.applicationContext.getResource(sqlResourcePath);
        SimpleJdbcTestUtils.executeSqlScript(this.simpleJdbcTemplate, new EncodedResource(resource,
                this.sqlScriptEncoding), continueOnError);
    }

    protected IDataSet getDatabaseDataSet(IDatabaseConnection connection, String[] tables, boolean forwardonly)
            throws DatabaseUnitException {
        if (logger.isDebugEnabled()) {
            logger.debug("getDatabaseDataSet(connection="
                    + connection + ", tables=" + tables + ", forwardonly=" + forwardonly + ") - start");
        }

        try {
            // Setup the ResultSet table factory
            IResultSetTableFactory factory = null;
            if (forwardonly) {
                factory = new ForwardOnlyResultSetTableFactory();
            } else {
                factory = new CachedResultSetTableFactory();
            }
            DatabaseConfig config = connection.getConfig();
            // config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
            // true);
            config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, factory);

            // Retrieve the complete database if no tables or queries specified.
            if (tables == null
                    || tables.length == 0) {
                return connection.createDataSet();
            }

            List<QueryDataSet> queryDataSets = new ArrayList<QueryDataSet>();

            QueryDataSet queryDataSet = new QueryDataSet(connection);

            for (String item : tables) {
                queryDataSet.addTable(item);
            }

            if (queryDataSet.getTableNames().length > 0) {
                queryDataSets.add(queryDataSet);
            }

            IDataSet[] dataSetsArray = new IDataSet[queryDataSets.size()];
            return new CompositeDataSet(queryDataSets.toArray(dataSetsArray));
        } catch (SQLException e) {
            logger.error("getDatabaseDataSet()", e);

            throw new DatabaseUnitException(e);
        }
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    /**
     *
     * @param url
     * @param forwardonly
     * @return
     * @throws DatabaseUnitException
     */
    protected IDataSet getSrcDataSet(URL url, boolean forwardonly) throws DatabaseUnitException {
        InputStream stream = null;
        if (ResourceUtils.isJarURL(url)) {
            stream = ClassUtils.getDefaultClassLoader().getResourceAsStream(url.toString());
        }

        IDataSetProducer producer = null;

        if (stream == null) {
            producer = new XmlProducer(new InputSource(url.toString()));
        } else {
            producer = new XmlProducer(new InputSource(stream));
        }

        if (forwardonly) {
            return new StreamingDataSet(producer);
        }
        return new CachedDataSet(producer);

    }

    protected IDataSet getSrcDataSet(URL url, ProducerType format, boolean forwardonly) throws DatabaseUnitException {
        if (logger.isDebugEnabled()) {
            logger.debug("getSrcDataSet(src="
                    + url + ", format=" + format + ", forwardonly=" + forwardonly + ") - start");
        }

        InputStream stream = null;
        if (ResourceUtils.isJarURL(url)) {
            stream = ClassUtils.getDefaultClassLoader().getResourceAsStream(url.toString());
        }

        IDataSetProducer producer = null;
        if (format == ProducerType.Xml) {
            if (stream == null) {
                producer = new XmlProducer(new InputSource(url.toString()));
            } else {
                producer = new XmlProducer(new InputSource(stream));
            }
        } else if (format == ProducerType.Cvs) {
            producer = new CsvProducer(url.toString());
        } else if (format == ProducerType.Flat) {
            if (stream == null) {
                producer = new FlatXmlProducer(new InputSource(url.toString()));
            } else {
                producer = new FlatXmlProducer(new InputSource(stream));
            }
        } else if (format == ProducerType.Dtd) {
            if (stream == null) {
                producer = new FlatDtdProducer(new InputSource(url.toString()));
            } else {
                producer = new FlatDtdProducer(new InputSource(stream));
            }
        } else {
            throw new IllegalArgumentException("Type must be either 'flat'(default), 'xml', 'csv' or 'dtd' but was: "
                    + format);
        }

        if (forwardonly) {
            return new StreamingDataSet(producer);
        }
        return new CachedDataSet(producer);

    }

    private String[] getTableNames(boolean reverse) {
        Collection<DbEntity> entities = getCayenneRuntimeContext().getDataDomain().getEntityResolver().getDbEntities();
        ArrayList<String> tables = new ArrayList<String>(entities.size());
        for (DbEntity dbEntity : entities) {
            if (tables.contains(dbEntity.getFullyQualifiedName())) {
                continue;
            }
            addTableName(tables, dbEntity);
        }
        if (reverse) {
            Collections.reverse(tables);
        }
        return tables.toArray(new String[tables.size()]);
    }

    public void loadDataBase() throws Exception {
        if (this.dataSetDBUnit == null) {
            return;
        }
        truncateDataBase();
        for (String fileName : dataSetDBUnit) {
            URL url = ResourceUtils.getURL(fileName);
            if (logger.isDebugEnabled()) {
                logger.debug("loadDataBase(file="
                        + fileName + ", translate=" + url + ")");
            }
            loadDataSet(url);
        }

    }

    protected void loadDataSet(URL url) throws SQLException, IOException, DatabaseUnitException {
        // initialize your database connection here
        IDatabaseConnection connection = new DatabaseConnection(DataSourceUtils.getConnection(getDataSource()));
        // DatabaseConfig config = connection.getConfig();
        // config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
        // true);
        // initialize your dataset here
        IDataSet dataSet = getSrcDataSet(url, producerType, false);
        try {
            DatabaseOperation.INSERT.execute(connection, dataSet);
        } finally {
            DataSourceUtils.releaseConnection(connection.getConnection(), getDataSource());
        }
    }

    /**
     * Executes a set of commands to drop/create database objects.
     */
    protected boolean safeExecute(String sql) throws SQLException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
            return true;
        } catch (SQLException ex) {
            return false;
        } finally {
            statement.close();
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    /**
     * Set the DataSource, typically provided via Dependency Injection.
     */
    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
        if (this.dataSource != null) {
            Connection con = null;
            try {
                con = DataSourceUtils.getConnection(dataSource);
                adapter = AdapterFactory.getCurrentAdapter(con);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DataSourceUtils.releaseConnection(con, dataSource);
            }
        }
    }

    /**
     * Specify the encoding for SQL scripts, if different from the platform
     * encoding.
     *
     * @see #executeSqlScript
     */
    public void setSqlScriptEncoding(String sqlScriptEncoding) {
        this.sqlScriptEncoding = sqlScriptEncoding;
    }

    protected void truncateDataBase() {
        this.deleteFromTables(getTableNames(false));
    }

}
