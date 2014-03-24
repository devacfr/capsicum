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

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.graph.CompoundDiff;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.commons.collections.Transformer;
import org.cfr.capsicum.datasource.IDataSourceOperations;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringDataDomain extends DataDomain {

    @Inject
    private IDataSourceOperations transationOperation;

    public SpringDataDomain(String name) {
        super(name);
    }

    public SpringDataDomain(String name, Map<String, String> properties) {
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
     */
    @Override
    Object runInTransaction(final Transformer operation) {
        if (!this.isUsingExternalTransactions()) {
            return super.runInTransaction(operation);
        } else {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                return operation.transform(null);
            }
            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            return transationOperation.execute(transactionDefinition, new TransactionCallback<Object>() {

                @Override
                public Object doInTransaction(TransactionStatus status) {
                    return operation.transform(status);
                }
            });
        }
    }

    @Override
    GraphDiff onSyncRollback(ObjectContext originatingContext) {
        // [devacfr] remove local rollback transaction
        return new CompoundDiff();
    }

}
