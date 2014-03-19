package org.cfr.capsicum.propertyset;

import java.util.Collection;
import java.util.Map;

import org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy;
import org.cfr.capsicum.propertyset.CayenneRuntimeContextProvider;
import org.cfr.capsicum.propertyset.ICayenneConfigurationProvider;
import org.cfr.capsicum.propertyset.ICayennePropertySetDAO;
import org.cfr.capsicum.propertyset.PropertySetItem;
import org.cfr.capsicum.test.AbstractSimpleCayenneJUnitTests;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.opensymphony.module.propertyset.PropertySet;

public class CayennePropertySetDAOImplTest extends AbstractSimpleCayenneJUnitTests {

    private static CayenneRuntimeContextProvider provider;

    @BeforeClass
    public static void init() {
        provider = new CayenneRuntimeContextProvider();
        Map<String, Object> config = new ImmutableMap.Builder<String, Object>().put(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY,
            createDatasource())
                .put(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY, getDbAdapter().getCanonicalName())
                .put(ICayenneConfigurationProvider.SCHEMA_UPDATE_STRATEGY_PROPERTY_KEY, CreateIfNoSchemaStrategy.class)
                .build();
        // test before
        assertNull(provider.getCayenneRuntimeContext());
        provider.setup(config);
        // test after
        assertNotNull(provider.getCayenneRuntimeContext());

    }

    @Test
    public void createPropertySetItemAndSave() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        PropertySetItem expected = propertySetDAO.create("Foo", 1, "foo.name");
        propertySetDAO.save(expected);

        // verify is stored
        PropertySetItem actual = propertySetDAO.findByKey("Foo", 1L, "foo.name");
        assertNotNull(actual);
        assertEquals(expected, actual);

        actual = propertySetDAO.findByKey("Foo", 1L, "foo");
        assertNull(actual);
    }

    @Test
    public void getAllKeys() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name");
        Creator.create(entityName, entityId, "foo.login");
        Creator.create(entityName, entityId, "foo.firstname");
        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, null, -1);
        assertEquals(3, keys.size());
    }

    @Test
    public void getAllKeysWithType() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.login", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.firstname", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.activated", PropertySet.BOOLEAN);
        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, null, PropertySet.STRING);
        assertEquals(3, keys.size());

        keys = propertySetDAO.getKeys(entityName, entityId, null, PropertySet.BOOLEAN);
        assertEquals(1, keys.size());
    }

    @Test
    public void getAllKeysWithTypeLike() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.login", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.firstname", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.activated", PropertySet.BOOLEAN);
        Creator.create(entityName, 2L, "foo.activated", PropertySet.BOOLEAN);

        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, "foo", PropertySet.STRING);
        assertEquals(3, keys.size());
    }

    @Test
    public void getAllKeysLike() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.login", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.firstname", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.activated", PropertySet.BOOLEAN);
        Creator.create(entityName, 2L, "foo.activated", PropertySet.BOOLEAN);

        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, "foo", -1);
        assertEquals(4, keys.size());
    }

    @Test
    public void removeAllOfEntity() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.login", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.firstname", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo.activated", PropertySet.BOOLEAN);
        propertySetDAO.remove(entityName, entityId);
        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, null, -1);
        assertEquals(0, keys.size());
    }

    @Test
    public void removeAllOfEntityByKey() {
        ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
        propertySetDAO.removeAll();
        String entityName = "Foo";
        Long entityId = 1L;
        Creator.create(entityName, entityId, "foo.name", PropertySet.STRING);
        Creator.create(entityName, entityId, "foo1.activated", PropertySet.BOOLEAN);
        propertySetDAO.remove(entityName, entityId, "foo.name");
        Collection<String> keys = propertySetDAO.getKeys(entityName, entityId, null, -1);
        assertEquals(1, keys.size());
    }

    public static class Builder {

        protected String entityName;

        protected Long entityId;

        protected int type;

        protected String key;

        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public PropertySetItem build() {
            PropertySetItem item = new PropertySetItem();
            item.setEntityId(entityId);
            item.setEntityName(entityName);
            item.setPropertyType(type);
            item.setPropertyKey(key);
            return null;
        }
    }

    public static class Creator extends Builder {

        public static PropertySetItem create(String entityName, long entityId, String key) {
            return ((Creator) new Creator().entityName(entityName).entityId(entityId).key(key)).create();
        }

        public static PropertySetItem create(String entityName, long entityId, String key, int type) {
            return ((Creator) new Creator().entityName(entityName).entityId(entityId).key(key).type(type)).create();
        }

        public PropertySetItem create() {
            ICayennePropertySetDAO propertySetDAO = provider.getPropertySetDAO();
            PropertySetItem item = propertySetDAO.create(entityName, entityId, key);
            item.setPropertyType(type);
            propertySetDAO.save(item);
            return item;
        }
    }
}
