// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
package io.helidon.examples.quickstart.mp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.helidon.microprofile.server.RoutingPath;
import io.helidon.security.SecurityContext;
import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;

@ApplicationScoped
@RoutingPath("/reactive")
public class ReactiveService implements Service {
    @Inject
    private SecurityContext securityContext;

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", (req, res) -> res.send("Context: " + securityContext));
    }
}
