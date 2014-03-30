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
package org.cfr.capsicum.support;

import org.cfr.capsicum.spring.support.CayenneOperationException;

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
