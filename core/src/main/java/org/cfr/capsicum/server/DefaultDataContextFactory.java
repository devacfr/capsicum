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
package org.cfr.capsicum.server;

import javax.annotation.Nonnull;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.access.TransactionalDataContext;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Injector;

/**
 * @author devacfr<christophefriederich@mac.com>
 *
 */
public class DefaultDataContextFactory extends DataContextFactory {

    @Inject
    private Injector injector;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataContext newInstance(@Nonnull final DataChannel parent, @Nonnull final ObjectStore objectStore) {
        DataContext dataContext = new TransactionalDataContext(parent, objectStore);
        injector.injectMembers(dataContext);
        return dataContext;
    }
}
