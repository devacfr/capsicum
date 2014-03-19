package org.cfr.capsicum.support.impl;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.cfr.capsicum.support.IEntrepriseObject;
import org.cfr.commons.util.Assert;


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

    @SuppressWarnings("unchecked")
    protected <U> U geInternalId(String columnName) {
        return (getObjectId() != null && !getObjectId().isTemporary()) ? (U) getObjectId().getIdSnapshot()
                .get(columnName) : null;
    }

    /**
     * Convenience method to get an id that may be used by the view. There is no setter as id is managed by Cayenne.
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