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

        Config config = buildConfig();

        logConfig(config);

        // subscribe using simple onChange consumer
        config.onChange(Main::logConfig);

        //Do this as there is bug in config/jpa
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        org.eclipse.microprofile.config.Config mpConfig = configProviderResolver.getBuilder().withSources(MpConfigSources.create(config)).build();
        configProviderResolver.registerConfig(mpConfig,null);

        io.helidon.microprofile.cdi.Main.main(args);
    }

    /**
     * Load the configuration from all sources.
     * @return the configuration root
     */
    static Config buildConfig() {
        return Config.
                builder(
                    ConfigSources.file("/conf/config-properties.yaml").pollingStrategy(PollingStrategies.regular(ofSeconds(5))))
                .disableEnvironmentVariablesSource()
                .disableSystemPropertiesSource()
                .build();
    }

    private static void logConfig(Config config) {
        LOGGER.info("Loaded config are: " + config.get("app.greeting"));
    }

}

