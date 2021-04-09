// Copyright (c) 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package io.helidon.examples.quickstart.mp;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Provider for greeting message based on configmap.
 */
@ApplicationScoped
public class ConfigProvider {

    private final Supplier<String> message;

    @Inject
    public ConfigProvider(@ConfigProperty(name = "config.greeting") Supplier<String> message) {
        this.message = message;
    }
    String getMessage() {
        return message.get();
    }

}
