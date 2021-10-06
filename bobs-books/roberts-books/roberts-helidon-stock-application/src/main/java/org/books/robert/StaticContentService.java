// Copyright (c) 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import io.helidon.microprofile.server.RoutingPath;
import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;
import io.helidon.webserver.staticcontent.StaticContentSupport;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RoutingPath("/")
public class StaticContentService implements Service {

    @Override
    public void update(Routing.Rules rules) {
        // static content reading from classpath location "web"
        rules.register(StaticContentSupport.builder("web")
                        .welcomeFileName("index.html"))

                // fallback always returns "web/index.html" for any request
                .register(StaticContentSupport.builder("web")
                        .pathMapper(path -> "index.html"));
    }
}
