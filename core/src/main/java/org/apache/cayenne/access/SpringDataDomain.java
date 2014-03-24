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
package org.apache.cayenne.access;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.graph.CompoundDiff;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.commons.collections.Transformer;
import org.cfr.capsicum.datasource.ITransactionOperationSupport;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This class extends {@link DataDomain} allowing the support of spring transaction.
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 */
public class SpringDataDomain extends DataDomain {

    /**
     * Allows the operation execution in transaction.
     */
    @Inject
    private ITransactionOperationSupport transationOperation;

    /**
     * create new naming DataDomain instance.
     * @param name the name of DataDomain
     */
    public SpringDataDomain(@Nonnull final String name) {
        super(name);
    }

    /**
     * create new naming DataDomain instance with properties.
     * @param name the name of DataDomain
     * @param properties properties
     */
    public SpringDataDomain(@Nonnull final String name, @Nullable final Map<String, String> properties) {
        super(name, properties);
    }

    @Override
    public Transaction createTransaction() {
        throw new UnsupportedOperationException("Oops. do not use cayenne transaction");
    }

    /**
     * Executes Transformer.transform() method in a transaction. Transaction
     * policy is to check for the thread transaction, and use it if one exists.
     * If it doesn't, a new transaction is created, with a scope limited to this
     * method.
     * @param operation a operation execute in current transaction
     * @return Returns a object.
     */
    @Override
    Object runInTransaction(@Nonnull final Transformer operation) {
        if (!this.isUsingExternalTransactions()) {
            return super.runInTransaction(operation);
        } else {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                return operation.transform(null);
            }
            // force the transaction execution
            // TODO [devacfr] maybe not good idea
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            return transationOperation.execute(transactionDefinition, new TransactionCallback<Object>() {

                @Override
                public Object doInTransaction(final TransactionStatus status) {
                    return operation.transform(status);
                }
            });
        }
    }

    @Override
    GraphDiff onSyncRollback(@Nonnull final ObjectContext originatingContext) {
        // [devacfr] remove local rollback transaction
        return new CompoundDiff();
    }

}
