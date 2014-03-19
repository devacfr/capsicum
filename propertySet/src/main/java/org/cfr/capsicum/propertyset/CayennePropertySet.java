package org.cfr.capsicum.propertyset;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableMap;
import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.util.Data;
import com.opensymphony.util.XMLUtils;

/**
 * This is the property set implementation for storing properties using Cayenne.
 * <p>
 *
 * <b>Required Args</b>
 * <ul>
 *  <li><b>entityId</b> - Long that holds the ID of this entity.</li>
 *  <li><b>entityName</b> - String that holds the name of this entity type</li>
 * </ul>
 * <p>
 *
 */
public class CayennePropertySet extends AbstractPropertySet {

    public static final String ENTITY_NAME_PROPERTY = "entityName";

    public static final String ENTITY_ID_PROPERTY = "entityId";

    protected static Logger log = LoggerFactory.getLogger(CayennePropertySet.class.getName());

    private ICayenneConfigurationProvider configProvider;

    private Long entityId;

    private String entityName;

    @Override
    public Collection<?> getKeys(String prefix, int type) throws PropertyException {
        return configProvider.getPropertySetDAO().getKeys(entityName, entityId, prefix, type);
    }

    @Override
    public int getType(String key) throws PropertyException {
        return findByKey(key).getPropertyType();
    }

    @Override
    public boolean exists(String key) throws PropertyException {
        try {
            if (findByKey(key) != null) {
                return true;
            }

            return false;
        } catch (PropertyException e) {
            return false;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void init(Map config, Map args) {
        super.init(config, args);
        this.entityId = (Long) args.get(ENTITY_ID_PROPERTY);
        this.entityName = (String) args.get(ENTITY_NAME_PROPERTY);

        // first let's see if we got given a configuration provider to use already
        configProvider = (ICayenneConfigurationProvider) args.get("configurationProvider");

        if (configProvider == null) // if we did not get given one in the args, we need to set a config provider up
        {
            ///CLOVER:OFF
            // lets see if we need to use a configurationProvider from a class
            configProvider = new CayenneRuntimeContextProvider();
            configProvider.setup(new ImmutableMap.Builder<String, Object>().putAll(config)
                    .put(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY, args.get(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY))
                    .put(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY, args.get(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY))
                    .build());
            ///CLOVER:ON
        } else {
            ///CLOVER:OFF
            if (log.isDebugEnabled()) {
                log.debug("Setting up property set with hibernate provider passed in args.");
            }
            ///CLOVER:ON
        }
    }

    @Override
    public void remove(String key) throws PropertyException {
        configProvider.getPropertySetDAO().remove(entityName, entityId, key);
    }

    @Override
    public void remove() throws PropertyException {
        configProvider.getPropertySetDAO().remove(entityName, entityId);
    }

    @Override
    public boolean supportsType(int type) {
        switch (type) {
            case PropertySet.OBJECT:
            case PropertySet.PROPERTIES:
                return false;
        }

        return true;
    }

    @Override
    protected void setImpl(int type, String key, Object value) throws PropertyException {
        PropertySetItem item = null;

        item = configProvider.getPropertySetDAO().findByKey(entityName, entityId, key);

        if (item == null) {
            item = configProvider.getPropertySetDAO().create(entityName, entityId.longValue(), key);
        } else if (item.getPropertyType().intValue() != type) {
            throw new PropertyException("Existing key '" + key + "' does not have matching type of " + type);
        }

        switch (type) {
            case BOOLEAN:
                item.setValueBoolean(((Boolean) value).booleanValue());

                break;

            case DOUBLE:
                item.setValueDecimal(new BigDecimal((Double) value));

                break;

            case STRING:
            case TEXT:
                item.setValueString((String) value);

                break;

            case LONG:
                item.setValueNumber(((Long) value).longValue());

                break;

            case INT:
                item.setValueNumber(((Integer) value).longValue());

                break;

            case DATE:
                item.setValueDate((Date) value);

                break;
            case DATA:
                item.setValueData(((Data) value).getBytes());

                break;
            case XML:
                try {
                    item.setValueString(XMLUtils.print((Document) value));
                } catch (IOException e) {
                    ///CLOVER:OFF
                    throw new PropertyException("Unexpected error when read xml document: " + e.getMessage());
                    ///CLOVER:ON
                }
                break;

            default:
                throw new PropertyException("type " + type + " not supported");
        }

        item.setPropertyType(type);

        configProvider.getPropertySetDAO().save(item);
    }

    @Override
    protected Object get(int type, String key) throws PropertyException {
        PropertySetItem item = findByKey(key);

        if (item == null) {
            return null;
        }

        if (item.getPropertyType() != type) {
            throw new PropertyException("key '" + key + "' does not have matching type of " + type);
        }

        switch (type) {
            case BOOLEAN:
                return item.getValueBoolean();

            case DOUBLE:
                return item.getValueDecimal().doubleValue();

            case STRING:
            case TEXT:
                return item.getValueString();

            case LONG:
                return item.getValueNumber();

            case INT:
                return item.getValueNumber().intValue();

            case DATE:
                return item.getValueDate();
            case DATA:
                return item.getValueData();
            case XML:
                try {
                    return XMLUtils.parse(item.getValueString());
                } catch (Exception e) {
                    ///CLOVER:OFF
                    throw new PropertyException("Unexpected error when read xml document from database: " + e.getMessage());
                    ///CLOVER:ON
                }
        }

        throw new PropertyException("type " + type + " not supported");
    }

    private PropertySetItem findByKey(String key) throws PropertyException {
        return configProvider.getPropertySetDAO().findByKey(entityName, entityId, key);
    }
}
