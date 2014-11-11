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
package org.cfr.capsicum.support.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.cfr.capsicum.support.IEntrepriseObject;
import org.cfr.commons.util.Assert;

/**
 * 
 * @author devacfr<christophefriederich@mac.com>
 * @since
 */
@SuppressWarnings("serial")
public abstract class EntrepriseObject extends CayenneDataObject implements IEntrepriseObject {

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete() {
        ObjectContext context = getObjectContext();
        Assert.notNull(context);
        context.deleteObjects(this);
    }

    /**
     * Gets the internal identifier.
     * @param columnName the column name of primary key.
     * @param <U> the type of identifier
     * @return Returns the internal id.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected <U> U geInternalId(@Nonnull final String columnName) {
        return getObjectId() != null && !getObjectId().isTemporary() ? (U) getObjectId().getIdSnapshot()
                .get(columnName) : null;
    }

    /**
     * Convenience method to get an id that may be used by the view. There is no setter as id is managed by Cayenne.
     * @return Returns the identifier primary key managed by cayenne.
     */
    protected long getPkId() {
        return isNew() ? -1 : Cayenne.longPKForObject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        return getPersistenceState() == PersistenceState.TRANSIENT || getPersistenceState() == PersistenceState.NEW;
    }
}