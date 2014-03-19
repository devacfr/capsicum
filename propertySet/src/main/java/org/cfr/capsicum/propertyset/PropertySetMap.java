package org.cfr.capsicum.propertyset;

import org.cfr.capsicum.propertyset.auto._PropertySetMap;

public class PropertySetMap extends _PropertySetMap {

    private static PropertySetMap instance;

    private PropertySetMap() {}

    public static PropertySetMap getInstance() {
        if(instance == null) {
            instance = new PropertySetMap();
        }

        return instance;
    }
}
