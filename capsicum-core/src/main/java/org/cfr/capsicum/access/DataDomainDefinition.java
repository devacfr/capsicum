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
package org.cfr.capsicum.access;

import javax.annotation.Nonnull;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.commons.util.Assert;

/**
 * <p>this class allows to override the {@link org.apache.cayenne.configuration.DataNodeDescriptor}
 * properties of cayenne configuration.<br>
 * and this class, the name of domain is mandatory.</p>
 * @author devacfr<christophefriederich@mac.com>
 * @since 1.0
 * @see org.apache.cayenne.configuration.DataNodeDescriptor
 */
public class DataDomainDefinition {

    /**
     * the Resource file containing the data domain description.
     */
    private String location;

    /**
     *
     */
    private String name;

    /**
     * Default constructor.
     */
    public DataDomainDefinition() {
    }

    /**
     * Constructs this class with the name of domain. the name can not be null or empty.
     * @param name the name of domain.
     */
    public DataDomainDefinition(@Nonnull final String name) {
        this.name = Assert.hasText(name);
    }

    /**
     * Gets the resource location of domain.
     * @return Returns the resource location of domain.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the resource location of domain.
     * @param resource the resource location of domain.
     */
    public void setLocation(@Nonnull final String resource) {
        this.location = Assert.hasText(resource);
    }

    /**
     * Gets the name of domain.
     * @return returns the name of domain.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of domain.
     * @param name name of domain
     */
    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    public void configure(@Nonnull final ICayenneRuntimeContext cayenneRuntimeContext,
                          @Nonnull final DataNodeDescriptor dataNodeDescriptor) {

    }

    public static Builder<? extends DataDomainDefinition> builder() {
        return new Builder<DataDomainDefinition>();
    }

    public static class Builder<T extends DataDomainDefinition> extends AbstractBuilder<DataDomainDefinition> {

        private String name;

        private String location;

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> location(String location) {
            this.location = location;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected T self() {
            return (T) new DataDomainDefinition();
        }

        public DataDomainDefinition build() {
            T definition = self();
            definition.setName(name);
            definition.setLocation(location);
            return definition;
        }
    }
}
