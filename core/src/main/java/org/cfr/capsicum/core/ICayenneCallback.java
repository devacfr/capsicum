package org.cfr.capsicum.core;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;

/**
 * An interface allowing to execute Cayenne operations via CayenneTemplate. This
 * allows CayenneTemplate to wrap normal Cayenne application code, providing
 * exception handling, DataContext injection, and other Spring services.
 * 
 * @author devacfr
 * @since 1.0
 * 
 */
public interface ICayenneCallback {

    public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException;
}