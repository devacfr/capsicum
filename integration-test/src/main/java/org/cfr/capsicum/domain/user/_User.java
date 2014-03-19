package org.cfr.capsicum.domain.user;

import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _User was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _User extends CayenneDataObject {

    @Deprecated
    public static final String FIRST_NAME_PROPERTY = "firstName";
    @Deprecated
    public static final String LAST_NAME_PROPERTY = "lastName";
    @Deprecated
    public static final String ADDRESSES_PROPERTY = "addresses";

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> FIRST_NAME = new Property<String>("firstName");
    public static final Property<String> LAST_NAME = new Property<String>("lastName");
    public static final Property<List<Address>> ADDRESSES = new Property<List<Address>>("addresses");

    public void setFirstName(String firstName) {
        writeProperty("firstName", firstName);
    }
    public String getFirstName() {
        return (String)readProperty("firstName");
    }

    public void setLastName(String lastName) {
        writeProperty("lastName", lastName);
    }
    public String getLastName() {
        return (String)readProperty("lastName");
    }

    public void addToAddresses(Address obj) {
        addToManyTarget("addresses", obj, true);
    }
    public void removeFromAddresses(Address obj) {
        removeToManyTarget("addresses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Address> getAddresses() {
        return (List<Address>)readProperty("addresses");
    }


}
