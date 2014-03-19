package org.cfr.capsicum.support;

import org.springframework.dao.DataAccessException;

public class EntityNotFoundException extends DataAccessException {

    /**
     * 
     */
    private static final long serialVersionUID = -3768272454302720874L;

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

}
