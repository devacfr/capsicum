package org.cfr.capsicum.support.xstream;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;

import org.apache.cayenne.DataObject;

import com.thoughtworks.xstream.converters.javabean.BeanProvider;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.mapper.Mapper;

public class CayenneObjectConverter extends JavaBeanConverter {

    static class BeanProviderOverride extends BeanProvider {

        @Override
        protected boolean canStreamProperty(PropertyDescriptor descriptor) {
            return !CayenneObjectConverter.OMITTED_FIELD_CAYENNE.contains(descriptor.getName());
        }
    }

    public final static Collection<String> OMITTED_FIELD_CAYENNE = Arrays.asList(new String[] { "objectContext", "dataContext", "objEntity",
            "objectId", "persistenceState", "snapshotVersion" });

    public CayenneObjectConverter(Mapper mapper) {
        super(mapper, new BeanProviderOverride());
    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return DataObject.class.isAssignableFrom(type);
    }
}
