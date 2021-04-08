// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package io.helidon.examples.quickstart.mp;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.LogManager;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.mp.MpConfigSources;
import io.helidon.config.PollingStrategies;
import io.helidon.microprofile.server.Server;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import static java.time.Duration.ofSeconds;

/**
 * The application main class.
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
     * @param args command line arguments
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        String isConfig = System.getenv("IS_CONFIG");

        // load logging configuration
        setupLogging();

        if (isConfig != null) {
            LOGGER.info("Starting server based on custom configmap configuration");
            // start the server
            startConfigServer(args);
        } else {
            LOGGER.info("Starting server based on default configuration");
            // start the server
            Server server = startServer();
            System.out.println("http://localhost:" + server.port() + "/greet");
        }
    }

    /**
     * Configure logging from logging.properties file.
     */
    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }

    /**
     * Start the server.
     * @return the created {@link Server} instance
     */
    static Server startServer() {
        // Server will automatically pick up configuration from
        // microprofile-config.properties
        // and Application classes annotated as @ApplicationScoped
        return Server.create().start();
    }

    /**
     * Start the server with custom config.
     */
    static void startConfigServer(String[] args) {
        // Build custom Helidon SE config based on file source and poll for changes.
        // Doing this as existing MpConfigSources.create(path) doesn't support watch/poll
        // Other option is to build a custom MpConfigSource and implement watch/poll on it
        Config config = Config.
                builder(
                        ConfigSources.file("/conf/config-properties.yaml").pollingStrategy(PollingStrategies.regular(ofSeconds(5))))
                .disableEnvironmentVariablesSource()
                .disableSystemPropertiesSource()
                .build();
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
     * Log the change in config
     * @param config object passed here is the one which is generated onChnage
     */
    private static void logConfig(Config config) {
        LOGGER.info("Loaded config are: " + config.get("config.greeting"));
    }
}
