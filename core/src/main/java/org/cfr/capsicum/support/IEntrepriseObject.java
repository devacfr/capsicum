package org.cfr.capsicum.support;


public interface IEntrepriseObject {

    /**
     * Schedules a persistent object for deletion on next commit.
     *
     * @throws CayenneOperationException if a deny delete rule is applicable for object deletion.
     */
    void delete();

    /**
     * Gets a value indicating whether this object is no persistent.
     *
     * @return <b>true</b> if the object is new; otherwise, <b>false</b>.
     */
    boolean isNew();
}
