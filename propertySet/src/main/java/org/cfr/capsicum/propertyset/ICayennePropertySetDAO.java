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

import java.util.Collection;

public interface ICayennePropertySetDAO {

    /**
    * Save the implementation of a PropertySetItem.
    *
    * @param item
    * @param isUpdate Boolean indicating whether or not this item already exists
    */
    void save(PropertySetItem item);

    Collection<String> getKeys(String entityName, Long entityId, String prefix, int type);

    PropertySetItem create(String entityName, long entityId, String key);

    PropertySetItem findByKey(String entityName, Long entityId, String key);

    void remove(String entityName, Long entityId, String key);

    void remove(String entityName, Long entityId);

    /**
     * for testing
     */
    void removeAll();
}