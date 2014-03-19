package org.cfr.capsicum.support;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * Generic uncategorized Cayenne exception.
 */
public class CayenneOperationException extends UncategorizedDataAccessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7574986417411614060L;

	public CayenneOperationException(String message, Throwable ex) {
        super(message, ex);
    }
}