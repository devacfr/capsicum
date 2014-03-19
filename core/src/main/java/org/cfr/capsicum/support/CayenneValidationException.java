package org.cfr.capsicum.support;

import java.util.List;

import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.SimpleValidationFailure;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.springframework.dao.DataAccessException;

/**
 * A exception thrown on Cayenne DataObject validation failures during commit.
 * Wraps Cayenne VaidationException or DeleteDenyException.
 */
public class CayenneValidationException extends DataAccessException {

    /**
     * 
     */
    private static final long serialVersionUID = -3086466451138061899L;

    protected ValidationResult validationResult;

    public CayenneValidationException(org.apache.cayenne.validation.ValidationResult validationResult) {
        super(validationResult != null ? validationResult.toString() : null);
        this.validationResult = convertResult(validationResult);
    }

    public CayenneValidationException(org.apache.cayenne.validation.ValidationException ex) {
        super(ex.getMessage());
        validationResult = (ex != null) ? convertResult(ex.getValidationResult()) : null;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static ValidationResult convertResult(org.apache.cayenne.validation.ValidationResult result) {
        ValidationResult validationResult = new ValidationResult();
        List<org.apache.cayenne.validation.ValidationFailure> failures = result.getFailures();
        for (org.apache.cayenne.validation.ValidationFailure validationFailure : failures) {
            ValidationFailure f = null;
            if (validationFailure instanceof org.apache.cayenne.validation.BeanValidationFailure) {
                org.apache.cayenne.validation.BeanValidationFailure v = (org.apache.cayenne.validation.BeanValidationFailure) validationFailure;
                f = new BeanValidationFailure(v.getSource(), v.getProperty(), v.getError());
            } else {
                org.apache.cayenne.validation.SimpleValidationFailure v = (org.apache.cayenne.validation.SimpleValidationFailure) validationFailure;
                f = new SimpleValidationFailure(v.getSource(), v.getError());
            }
            validationResult.addFailure(f);
        }
        return validationResult;
    }
}