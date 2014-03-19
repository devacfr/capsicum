package org.cfr.capsicum.propertyset;

import java.util.Map;

/**
 * Use this class to provide your own configurations to the PropertySet Cayenne providers.
 * <p>
 * Simply implement this interface and return a Cayenne Configuration object.
 * <p>
 * This is setup by using the configuration.provider.class property, with the classname
 * of your implementation.
 */
public interface ICayenneConfigurationProvider {

    public static final String DATASOURCE_PROPERTY_KEY = "datasource";

    public static final String ADAPTER_PROPERTY_KEY = "adapter";

    public static final String SCHEMA_UPDATE_STRATEGY_PROPERTY_KEY = "schemaUpdateStrategy";

    public static final String DOMAIN_NAME = "propertyset";

    ICayennePropertySetDAO getPropertySetDAO();

    /**
    * Setup a Hibernate configuration object with the given properties.
    *
    * This will always be called before getConfiguration().
    */
    void setup(Map<String, Object> properties);
}
