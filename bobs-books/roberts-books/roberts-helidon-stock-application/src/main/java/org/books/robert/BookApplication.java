// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationScoped
@ApplicationPath("/api")
public class BookApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
        // ZipkinProxy.class,
                                       BookResource.class,
                                       AuthorResource.class,
                                       OrderResource.class);
    }
}
