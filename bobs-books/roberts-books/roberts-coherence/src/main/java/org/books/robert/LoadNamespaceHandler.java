// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.net.URI;
import java.util.Set;

import com.tangosol.config.xml.AbstractNamespaceHandler;
import com.tangosol.config.xml.ProcessingContext;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.InterceptorRegistry;
import com.tangosol.net.events.application.LifecycleEvent;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.util.RegistrationBehavior;
import com.tangosol.util.ResourceRegistry;

public class LoadNamespaceHandler extends AbstractNamespaceHandler {

    @Override
    public void onStartNamespace(ProcessingContext context, XmlElement element, String prefix, URI uri) {
        super.onStartNamespace(context, element, prefix, uri);

        // register custom data loaders
        registerProcessor("books", new BookLoader());

        // need to delay data loading until all the services are started and the CCF is activated
        // register event interceptor that will be triggered on CCF activation, at which point it is safe to load the data
        ResourceRegistry resourceRegistry = context.getResourceRegistry();
        InterceptorRegistry interceptorRegistry = resourceRegistry.getResource(InterceptorRegistry.class);

        //noinspection Convert2Lambda
        interceptorRegistry.registerEventInterceptor(new EventInterceptor<LifecycleEvent>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(LifecycleEvent event) {
                if (event.getType() == LifecycleEvent.Type.ACTIVATED) {
                    // CCF has been activated, get all registered loaders and load the data
                    ConfigurableCacheFactory ccf = event.getConfigurableCacheFactory();
                    Set<Loader> loaders = resourceRegistry.getResource(Set.class, "loaders");
                    if (loaders != null) {
                        loaders.forEach(loader -> loader.load(ccf));
                    }
                }
            }
        }, RegistrationBehavior.IGNORE);
    }
}
