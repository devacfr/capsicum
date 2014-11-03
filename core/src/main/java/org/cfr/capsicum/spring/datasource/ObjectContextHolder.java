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
package org.cfr.capsicum.spring.datasource;

import javax.annotation.Nonnull;

import org.apache.cayenne.ObjectContext;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 *
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class ObjectContextHolder extends ResourceHolderSupport {

    private ObjectContext baseContext;

    private boolean transactionActive;

    /**
     * Create a new ConnectionHolder for the given ConnectionHandle.
     * @param connectionHandle the ConnectionHandle to hold
     */
    public ObjectContextHolder(final ObjectContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        this.baseContext = null;
    }

    @Nonnull
    public ObjectContext getObjectContext() {
        return baseContext;
    }

    public void setObjectContext(@Nonnull final ObjectContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * @param transactionActive
     */
    public void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    /**
     * @return the transactionActive
     */
    public boolean isTransactionActive() {
        return transactionActive;
    }

}