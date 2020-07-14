// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tangosol.config.ConfigurationException;
import com.tangosol.config.xml.ElementProcessor;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.ResourceRegistry;

public abstract class AbstractLoader<K, V> implements ElementProcessor<Void>, Loader {
    private String cacheName;
    private String fileName;

    @SuppressWarnings("unchecked")
    @Override
    public Void process(ProcessingContext context, XmlElement xmlElement) throws ConfigurationException {
        cacheName = xmlElement.getQualifiedName().getLocalName();
        fileName  = xmlElement.getAttribute("file").getString();

        ResourceRegistry registry = context.getResourceRegistry();
        Set<Loader> loaders = registry.getResource(Set.class, "loaders");
        if (loaders == null) {
            loaders = new HashSet<>();
            registry.registerResource(Set.class, "loaders", loaders);
        }
        loaders.add(this);

        return null;
    }

    @Override
    public void load(ConfigurableCacheFactory ccf) {
        NamedCache<String, Boolean> loaders = ccf.ensureCache("loaders", null);
        if (loaders.lock(cacheName, 0)) {
            try {
                boolean fLoaded = loaders.getOrDefault(cacheName, false);
                if (!fLoaded) {
                    NamedCache<K, V> cache = ccf.ensureCache(cacheName, null);
                    cache.putAll(loadData(getClass().getClassLoader().getResourceAsStream(fileName)));
                    System.out.println("Loaded " + cache.size() + " entries into '" + cacheName + "' cache");
                    loaders.put(cacheName, true);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            finally {
                loaders.unlock(cacheName);
            }
        }
    }

    protected abstract Map<? extends K,? extends V> loadData(InputStream in) throws IOException;
}
