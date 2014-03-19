package org.cfr.capsicum.propertyset;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;

import com.google.common.collect.ImmutableMap;

public class CayennePropertySetDAOUtils {

    private static Map<String, String> mapQueries = new ImmutableMap.Builder<String, String>().put("all_keys",
        "select p.propertyKey from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId")
            .put("all_keys_with_type",
                "select p.propertyKey from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId and p.propertyType = :propertyType")
            .put("all_keys_like",
                "select p.propertyKey from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId and p.propertyKey like :propertyKey")
            .put("all_keys_with_type_like",
                "select p.propertyKey from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId and p.propertyType = :propertyType and p.propertyKey like :propertyKey")
            .build();

    @SuppressWarnings("unchecked")
    public static PropertySetItem getItem(ObjectContext context, String entityName, Long entityId, String propertyKey) {
        EJBQLQuery query = new EJBQLQuery(
                "select p from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId and p.propertyKey = :propertyKey");
        query.setParameter("entityName", entityName);
        query.setParameter("entityId", entityId);
        query.setParameter("propertyKey", propertyKey);
        List<PropertySetItem> list = context.performQuery(query);
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    /**
    * This is the body of the getKeys() method, so that you can reuse it
    * wrapped by your own session management.
    */
    @SuppressWarnings("unchecked")
    public static List<String> getKeysImpl(ObjectContext context, String entityName, Long entityId, String prefix, int type) {
        EJBQLQuery query;

        if ((prefix != null) && (type > 0)) {
            query = new EJBQLQuery(mapQueries.get("all_keys_with_type_like"));
            query.setParameter("propertyKey", prefix + '%');
            query.setParameter("propertyType", type);
        } else if (prefix != null) {
            query = new EJBQLQuery(mapQueries.get("all_keys_like"));
            query.setParameter("propertyKey", prefix + '%');
        } else if (type > 0) {
            query = new EJBQLQuery(mapQueries.get("all_keys_with_type"));
            query.setParameter("propertyType", type);
        } else {
            query = new EJBQLQuery(mapQueries.get("all_keys"));
        }

        query.setParameter("entityName", entityName);
        query.setParameter("entityId", entityId.longValue());

        return context.performQuery(query);
    }
}