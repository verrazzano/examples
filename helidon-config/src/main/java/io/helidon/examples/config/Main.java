// Copyright (c) 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
package io.helidon.examples.config;

import java.util.logging.Logger;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.mp.MpConfigSources;
import io.helidon.config.PollingStrategies;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import static java.time.Duration.ofSeconds;

/**
 * Main class to start the service.
 */
public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {

        // Build Helidon SE config
        // Doing this as existing MpConfigSources.create(path) doesn't support watch/poll
        // Other option is to build a custom MpConfigSource and implement watch/poll on it
        Config config = buildConfig();
        logConfig(config);
        // subscribe using simple onChange consumer
        // This is just to log the change, if any.
        config.onChange(Main::logConfig);

        // Do this as there is bug in config/jpa
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        // Build Microprofile config from Heldion SE config instance
        org.eclipse.microprofile.config.Config mpConfig = configProviderResolver.getBuilder().withSources(MpConfigSources.create(config)).build();
        configProviderResolver.registerConfig(mpConfig,null);

        io.helidon.microprofile.cdi.Main.main(args);
    }

    /**
     * Build custom Helidon SE config based on file source and poll for changes.
     * @return Helidon SE config
     */
    static Config buildConfig() {
        return Config.
                builder(
                    ConfigSources.file("/conf/config-properties.yaml").pollingStrategy(PollingStrategies.regular(ofSeconds(5))))
                .disableEnvironmentVariablesSource()
                .disableSystemPropertiesSource()
                .build();
    }

    /**
     * Log the change in config
     * @param config object passed here is the one which is generated onChnage
     */
    private static void logConfig(Config config) {
        LOGGER.info("Loaded config are: " + config.get("app.greeting"));
    }

}

