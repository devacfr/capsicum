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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.annotation.Nonnull;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.resource.Resource;
import org.cfr.commons.util.Assert;

/**
 * Decorator class allowing to manipulate cayenne resource with spring.
 * @author devacfr
 * @since 1.0
 */
public class SpringResource implements Resource, org.springframework.core.io.Resource {

    /**
     * 
     */
    private static final long serialVersionUID = 7138738421273158737L;

    private final org.springframework.core.io.Resource instance;

    /**
     * 
     * @param resource
     */
    public SpringResource(@Nonnull org.springframework.core.io.Resource resource) {
        this.instance = Assert.notNull(resource);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof SpringResource && this.instance.equals(((SpringResource) obj).instance);
    }

    @Override
    public URL getURL() {
        try {
            return this.instance.getURL();
        } catch (IOException e) {
            throw new CayenneRuntimeException("unexpected error", e);
        }
    }

    @Override
    public Resource getRelativeResource(final String relativePath) {
        org.springframework.core.io.Resource resource;
        try {
            resource = this.instance.createRelative(relativePath);
        } catch (IOException e) {
            throw new CayenneRuntimeException("Error creating relative resource '%s' : '%s'", e, this.instance,
                    relativePath);
        }
        return new SpringResource(resource);
    }

    @Override
    public String toString() {
        return instance.toString();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return instance.getInputStream();
    }

    @Override
    public boolean exists() {
        return instance.exists();
    }

    @Override
    public boolean isReadable() {
        return instance.isReadable();
    }

    @Override
    public boolean isOpen() {
        return instance.isOpen();
    }

    @Override
    public URI getURI() throws IOException {
        return instance.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return instance.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return instance.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return instance.lastModified();
    }

    @Override
    public org.springframework.core.io.Resource createRelative(final String relativePath) throws IOException {
        return instance.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return instance.getFilename();
    }

    @Override
    public String getDescription() {
        return instance.getDescription();
    }

}
