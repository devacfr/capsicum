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
package org.cfr.capsicum.propertyset;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.spring.support.CayenneTemplate;
import org.cfr.capsicum.spring.support.ICayenneCallback;
import org.cfr.commons.util.Assert;

public class CayennePropertySetDAOImpl extends CayenneTemplate implements ICayennePropertySetDAO {

    public CayennePropertySetDAOImpl(@Nonnull final ICayenneRuntimeContext context) {
        super(Assert.notNull(context));
    }

    @Override
    public void save(final PropertySetItem item) {
        this.execute(new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                registerNewObject(item);
                return item;
            }
        });
    }

    @Override
    public Collection<String> getKeys(String entityName, Long entityId, String prefix, int type) {
        return CayennePropertySetDAOUtils.getKeysImpl(this.getObjectContext(), entityName, entityId, prefix, type);
    }

    @Override
    public PropertySetItem create(String entityName, long entityId, String key) {
        PropertySetItem propertySetItem = new PropertySetItem();
        propertySetItem.setEntityName(entityName);
        propertySetItem.setEntityId(entityId);
        propertySetItem.setPropertyKey(key);
        return propertySetItem;
    }

    @Override
    public PropertySetItem findByKey(String entityName, Long entityId, String key) {
        return CayennePropertySetDAOUtils.getItem(this.getObjectContext(), entityName, entityId, key);
    }

    @Override
    public void remove(final String entityName, final Long entityId) {
        this.execute(new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                EJBQLQuery query = new EJBQLQuery(
                        "delete from PropertySetItem p where p.entityName = :entityName and p.entityId = :entityId");
                query.setParameter("entityName", entityName);
                query.setParameter("entityId", entityId);
                context.performGenericQuery(query);
                return null;
            }
        });
    }

    @Override
    public void remove(final String entityName, final Long entityId, final String propertyKey) {
        this.execute(new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                EJBQLQuery query = new EJBQLQuery(
                        "delete from PropertySetItem p "
                                + "where p.entityName = :entityName and p.entityId = :entityId and p.propertyKey = :propertyKey");
                query.setParameter("entityName", entityName);
                query.setParameter("entityId", entityId);
                query.setParameter("propertyKey", propertyKey);
                context.performGenericQuery(query);
                return null;
            }
        });
    }

    @Override
    public void removeAll() {
        this.execute(new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                EJBQLQuery query = new EJBQLQuery("delete from PropertySetItem p");
                context.performGenericQuery(query);
                return null;
            }
        });
    }

}