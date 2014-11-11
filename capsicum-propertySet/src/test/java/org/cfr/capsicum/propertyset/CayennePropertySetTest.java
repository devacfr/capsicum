package org.cfr.capsicum.propertyset;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cayenne.access.dbsync.CreateIfNoSchemaStrategy;
import org.cfr.capsicum.test.AbstractSimpleCayenneJUnitTests;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.util.XMLUtils;

public class CayennePropertySetTest extends AbstractSimpleCayenneJUnitTests {

    private final static String entityName = "foo";

    private final static long entityId = 1;

    private static PropertySet ps;

    private static ICayenneConfigurationProvider provider;

    @BeforeClass
    public static void init() {
        final Map<String, Object> config = new ImmutableMap.Builder<String, Object>().put(ICayenneConfigurationProvider.ADAPTER_PROPERTY_KEY,
            getDbAdapter().getCanonicalName())
                .put(ICayenneConfigurationProvider.DATASOURCE_PROPERTY_KEY, createDatasource())
                .put(ICayenneConfigurationProvider.SCHEMA_UPDATE_STRATEGY_PROPERTY_KEY, CreateIfNoSchemaStrategy.class)
                .build();

        provider = new CayenneRuntimeContextProvider();
        provider.setup(config);
        final Map<String, Object> props = new ImmutableMap.Builder<String, Object>().put(CayennePropertySet.ENTITY_ID_PROPERTY,
            entityId)
                .put(CayennePropertySet.ENTITY_NAME_PROPERTY, entityName)
                .put("configurationProvider", provider)
                .build();
        ps = PropertySetManager.getInstance("capsicum", props);
        assertEquals(CayennePropertySet.class, ps.getClass());

    }

    @Test
    public void exists() {
        ps.remove();
        assertEquals(false, ps.exists("foo"));
        ps.setString("foo", "nothing");
        assertEquals(true, ps.exists("foo"));
    }

    @Test
    public void removeKeys() {
        ps.remove();
        ps.setString("foo", "nothing");
        assertEquals(true, ps.exists("foo"));
        ps.remove("foo");
        assertEquals(false, ps.exists("foo"));
    }

    @Test
    public void supportsTypes() {
        assertEquals(true, ps.supportsTypes());
        assertEquals(true, ps.supportsType(PropertySet.BOOLEAN));
        assertEquals(true, ps.supportsType(PropertySet.DATA));
        assertEquals(true, ps.supportsType(PropertySet.DATE));
        assertEquals(true, ps.supportsType(PropertySet.DOUBLE));
        assertEquals(true, ps.supportsType(PropertySet.INT));
        assertEquals(true, ps.supportsType(PropertySet.LONG));
        assertEquals(false, ps.supportsType(PropertySet.OBJECT));
        assertEquals(false, ps.supportsType(PropertySet.PROPERTIES));
        assertEquals(true, ps.supportsType(PropertySet.STRING));
        assertEquals(true, ps.supportsType(PropertySet.TEXT));
        assertEquals(true, ps.supportsType(PropertySet.XML));
    }

    @Test
    public void globalTest() throws SAXException, IOException, ParserConfigurationException {
        ps.remove();
        String string = "nothing";
        Boolean bool = Boolean.TRUE;
        byte[] data = "normal".getBytes();
        Date date = new Date();
        long l = Long.MAX_VALUE;
        int i = Integer.MAX_VALUE;
        double d = 0.000000046546548578554654f;
        String text = "blalablbalblablaabal";
        Document xml = XMLUtils.parse("<root>\n<item>toto1</item>\n<item>toto2</item>\n</root>");

        assertEquals(true, ps.getKeys().isEmpty());
        ps.setString("foo.string", string);
        ps.setBoolean("foo.boolean", bool);

        ps.setData("foo.data", data);
        ps.setDate("foo.date", date);
        ps.setLong("foo.long", l);
        ps.setInt("foo.int", i);
        ps.setDouble("foo.double", d);
        ps.setText("foo.text", text);

        ps.setXML("foo.xml", xml);
        assertEquals(9, ps.getKeys().size());

        assertEquals(string, ps.getString("foo.string"));
        assertEquals(bool, ps.getBoolean("foo.boolean"));
        assertEquals(new String(data), new String(ps.getData("foo.data")));
        assertEquals(date, ps.getDate("foo.date"));
        assertEquals(l, ps.getLong("foo.long"));
        assertEquals(i, ps.getInt("foo.int"));
        assertEquals(d, ps.getDouble("foo.double"), 0.001f);
        assertEquals(text, ps.getText("foo.text"));
        assertEquals(XMLUtils.print(xml), XMLUtils.print(ps.getXML("foo.xml")));

        assertEquals(PropertySet.STRING, ps.getType("foo.string"));
    }

    @Test(expected = PropertyException.class)
    public void setPropertyWithWrongType() {
        ps.remove();
        ps.setText("foo", "toto");
        ps.setLong("foo", 11);
    }

    @Test(expected = PropertyException.class)
    public void getPropertyWithWrongType() {
        ps.remove();
        ps.setText("foo", "toto");
        ps.getLong("foo");
    }

    @Test(expected = PropertyException.class)
    public void setKeyWithUnsupporedType() {
        ps.remove();
        ps.setObject("foo", "toto");
    }

    @Test(expected = PropertyException.class)
    public void getExistingKeyWithUnsupporedType() {
        ps.remove();
        ps.setText("foo", "toto");
        ps.getObject("foo");
    }
}
