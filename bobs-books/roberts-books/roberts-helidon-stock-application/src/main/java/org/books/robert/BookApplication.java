// Copyright (c) 2020 Oracle and/or its affiliates.

package org.books.robert;

import io.helidon.common.CollectionsHelper;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationScoped
@ApplicationPath("/api")
public class BookApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(ZipkinProxy.class,
                                       BookResource.class,
                                       AuthorResource.class,
                                       OrderResource.class);
    }
}
