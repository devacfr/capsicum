package org.cfr.capsicum.propertyset.auto;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

/**
 * Class _PropertySetItem was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PropertySetItem extends CayenneDataObject {

    /**
     * 
     */
    private static final long serialVersionUID = -1228314562386231274L;

    public static final String ENTITY_ID_PROPERTY = "entityId";

    public static final String ENTITY_NAME_PROPERTY = "entityName";

    public static final String PROPERTY_KEY_PROPERTY = "propertyKey";

    public static final String PROPERTY_TYPE_PROPERTY = "propertyType";

    public static final String VALUE_BOOLEAN_PROPERTY = "valueBoolean";

    public static final String VALUE_DATA_PROPERTY = "valueData";

    public static final String VALUE_DATE_PROPERTY = "valueDate";

    public static final String VALUE_DECIMAL_PROPERTY = "valueDecimal";

    public static final String VALUE_NUMBER_PROPERTY = "valueNumber";

    public static final String VALUE_STRING_PROPERTY = "valueString";

    public static final String ID_PK_COLUMN = "ID";

    public void setEntityId(Long entityId) {
        writeProperty("entityId", entityId);
    }

    public Long getEntityId() {
        return (Long) readProperty("entityId");
    }

    public void setEntityName(String entityName) {
        writeProperty("entityName", entityName);
    }

    public String getEntityName() {
        return (String) readProperty("entityName");
    }

    public void setPropertyKey(String propertyKey) {
        writeProperty("propertyKey", propertyKey);
    }

    public String getPropertyKey() {
        return (String) readProperty("propertyKey");
    }

    public void setPropertyType(Integer propertyType) {
        writeProperty("propertyType", propertyType);
    }

    public Integer getPropertyType() {
        return (Integer) readProperty("propertyType");
    }

    public void setValueBoolean(Boolean valueBoolean) {
        writeProperty("valueBoolean", valueBoolean);
    }

    public Boolean getValueBoolean() {
        return (Boolean) readProperty("valueBoolean");
    }

    public void setValueData(byte[] valueData) {
        writeProperty("valueData", valueData);
    }

    public byte[] getValueData() {
        return (byte[]) readProperty("valueData");
    }

    public void setValueDate(Date valueDate) {
        writeProperty("valueDate", valueDate);
    }

    public Date getValueDate() {
        return (Date) readProperty("valueDate");
    }

    public void setValueDecimal(BigDecimal valueDecimal) {
        writeProperty("valueDecimal", valueDecimal);
    }

    public BigDecimal getValueDecimal() {
        return (BigDecimal) readProperty("valueDecimal");
    }

    public void setValueNumber(Long valueNumber) {
        writeProperty("valueNumber", valueNumber);
    }

    public Long getValueNumber() {
        return (Long) readProperty("valueNumber");
    }

    public void setValueString(String valueString) {
        writeProperty("valueString", valueString);
    }

    public String getValueString() {
        return (String) readProperty("valueString");
    }

}
