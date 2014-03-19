package org.cfr.capsicum.test;

import java.io.File;
import java.io.FileNotFoundException;

import javax.sql.DataSource;

import org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy;
import org.apache.cayenne.access.dbsync.SchemaUpdateStrategy;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.derby.DerbyAdapter;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.configuration.DataDomainDefinition;
import org.cfr.capsicum.server.ServerRuntimeFactoryBean;
import org.cfr.commons.util.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.Log4jConfigurer;

import com.google.common.collect.Lists;

public abstract class AbstractSimpleCayenneJUnitTests extends EasyMockTestCase {

    protected static final File WORK_HOME = new File(getBasedir(), "target/work");

    private static String basedir;

    /**
     * Logger available to subclasses.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static BasicDataSource datasource;

    private static ICayenneRuntimeContext context;

    private String domainFileLocation;

    private String domainName;

    private Class<SchemaUpdateStrategy> overrideStrategy;

    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AbstractSimpleCayenneJUnitTests() {
        super();
    }

    public AbstractSimpleCayenneJUnitTests(final String domainName, final String domainFileLocation) {
        super();
        this.domainFileLocation = Assert.hasText(domainFileLocation);
        this.domainName = Assert.hasText(domainName);
        try {
            context = createCayenneContext();
        } catch (Exception e) {
            throw new RuntimeException("Error when initialization of cayenne", e);
        }
    }

    public static String getBasedir() {
        if (basedir != null) {
            return basedir;
        }

        basedir = System.getProperty("basedir");

        if (basedir == null) {
            basedir = new File("").getAbsolutePath();
        }

        return basedir;
    }

    @BeforeClass
    public static void init() throws Exception {
        basedir = getBasedir();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        FileSystemUtils.deleteRecursively(WORK_HOME);
        WORK_HOME.mkdirs();
    }

    protected static DataSource createDatasource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(EmbeddedDriver.class.getCanonicalName());
        ds.setUrl("jdbc:derby:memory:testdb;create=true");
        datasource = ds;
        return ds;
    }

    protected ICayenneRuntimeContext getCayenneContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    protected ICayenneRuntimeContext createCayenneContext() throws Exception {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ServerRuntimeFactoryBean factory = new ServerRuntimeFactoryBean();
        factory.setDefaultSchemaUpdateStrategy((Class<SchemaUpdateStrategy>) (overrideStrategy != null ? overrideStrategy
                : CreateIfNoSchemaStrategy.class));
        factory.setDataSource(createDatasource());
        DataDomainDefinition dataDomainDefinition = new DataDomainDefinition();
        dataDomainDefinition.setName(domainName);
        dataDomainDefinition.setDomainResource(resourceLoader.getResource(domainFileLocation));
        factory.setDataDomainDefinitions(Lists.newArrayList(dataDomainDefinition));
        factory.afterPropertiesSet();
        return factory;
    }

    protected static Class<? extends DbAdapter> getDbAdapter() {
        return DerbyAdapter.class;
    }

    @AfterClass
    public static void shutdown() throws Exception {
        if (datasource != null)
            datasource.close();
        datasource = null;
        if (context instanceof DisposableBean)
            ((DisposableBean) context).destroy();
        context = null;
    }

}