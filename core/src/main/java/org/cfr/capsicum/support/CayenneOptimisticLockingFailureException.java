package org.cfr.capsicum.support;

import java.util.Map;

import org.apache.cayenne.access.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * A exception wrapping Cayenne optimistic locking failure.
 */
public class CayenneOptimisticLockingFailureException extends
        OptimisticLockingFailureException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7772788554255601404L;
	
	
	protected String querySQL;
    protected Map<String,Object> qualifierSnapshot;

    @SuppressWarnings("unchecked")
	public CayenneOptimisticLockingFailureException(OptimisticLockException ex) {
        super(ex.getUnlabeledMessage(), ex);

        this.querySQL = ex.getQuerySQL();
        this.qualifierSnapshot = ex.getQualifierSnapshot();
    }

    public Map<String,Object> getQualifierSnapshot() {
        return qualifierSnapshot;
    }

    public String getQuerySQL() {
        return querySQL;
    }
}