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