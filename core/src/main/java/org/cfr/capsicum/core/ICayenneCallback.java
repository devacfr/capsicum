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