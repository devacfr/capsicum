/**
 * Copyright 2014 devacfr<christophefriederich@mac.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfr.capsicum.support.xstream;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;

import org.apache.cayenne.DataObject;

import com.thoughtworks.xstream.converters.javabean.BeanProvider;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class CayenneObjectConverter extends JavaBeanConverter {

    static class BeanProviderOverride extends BeanProvider {

        @Override
        protected boolean canStreamProperty(PropertyDescriptor descriptor) {
            return !CayenneObjectConverter.OMITTED_FIELD_CAYENNE.contains(descriptor.getName());
        }
    }

    public final static Collection<String> OMITTED_FIELD_CAYENNE = Arrays.asList(new String[] { "objectContext",
            "dataContext", "objEntity", "objectId", "persistenceState", "snapshotVersion" });

    public CayenneObjectConverter(Mapper mapper) {
        super(mapper, new BeanProviderOverride());
    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return DataObject.class.isAssignableFrom(type);
    }
}
