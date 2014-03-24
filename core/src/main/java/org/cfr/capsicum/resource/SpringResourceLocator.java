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
package org.cfr.capsicum.resource;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.ResourceLocator;
import org.cfr.commons.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author devacfr
 * @since 1.0
 */
public class SpringResourceLocator implements ResourceLocator {

    private final ResourceLoader resourceLoader;

    /**
     * 
     * @param resourceLoader
     */
    @Autowired
    public SpringResourceLocator(@Nonnull ResourceLoader resourceLoader) {
        this.resourceLoader = Assert.notNull(resourceLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Resource> findResources(String name) {
        org.springframework.core.io.Resource rsrc = resourceLoader.getResource(name);
        return ImmutableList.<Resource> of(new SpringResource(rsrc));
    }

}