package org.cfr.capsicum.domain.user;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _Address was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Address extends CayenneDataObject {

    @Deprecated
    public static final String CITY_PROPERTY = "city";
    @Deprecated
    public static final String COUNTRY_PROPERTY = "country";
    @Deprecated
    public static final String STREET_PROPERTY = "street";
    @Deprecated
    public static final String USER_PROPERTY = "user";

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> CITY = new Property<String>("city");
    public static final Property<String> COUNTRY = new Property<String>("country");
    public static final Property<String> STREET = new Property<String>("street");
    public static final Property<User> USER = new Property<User>("user");

    public void setCity(String city) {
        writeProperty("city", city);
    }
    public String getCity() {
        return (String)readProperty("city");
    }

    public void setCountry(String country) {
        writeProperty("country", country);
    }
    public String getCountry() {
        return (String)readProperty("country");
    }

    public void setStreet(String street) {
        writeProperty("street", street);
    }
    public String getStreet() {
        return (String)readProperty("street");
    }

    public void setUser(User user) {
        setToOneTarget("user", user, true);
    }

    public User getUser() {
        return (User)readProperty("user");
    }


}