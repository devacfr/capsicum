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
