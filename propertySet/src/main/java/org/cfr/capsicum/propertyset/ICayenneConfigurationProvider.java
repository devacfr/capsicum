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
