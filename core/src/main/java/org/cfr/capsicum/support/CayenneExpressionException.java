package org.cfr.capsicum.support;

import org.apache.cayenne.exp.ExpressionException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Exception wrapping Cayenne ExpressionException.
 */
public class CayenneExpressionException extends
        InvalidDataAccessApiUsageException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1747876574019592478L;
	
	
	protected String expressionString;

    public CayenneExpressionException(ExpressionException ex) {
        super(ex.getUnlabeledMessage(), ex);
        this.expressionString = ex.getExpressionString();
    }

    public String getExpressionString() {
        return expressionString;
    }
}