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

import java.util.Map;

import org.apache.cayenne.access.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * A exception wrapping Cayenne optimistic locking failure.
 */
public class CayenneOptimisticLockingFailureException extends OptimisticLockingFailureException {

    /**
     * 
     */
    private static final long serialVersionUID = -7772788554255601404L;

    protected String querySQL;

    protected Map<String, Object> qualifierSnapshot;

    @SuppressWarnings("unchecked")
    public CayenneOptimisticLockingFailureException(OptimisticLockException ex) {
        super(ex.getUnlabeledMessage(), ex);

        this.querySQL = ex.getQuerySQL();
        this.qualifierSnapshot = ex.getQualifierSnapshot();
    }

    public Map<String, Object> getQualifierSnapshot() {
        return qualifierSnapshot;
    }

    public String getQuerySQL() {
        return querySQL;
    }
}