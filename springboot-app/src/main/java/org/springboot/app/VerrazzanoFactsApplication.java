// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.springboot.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VerrazzanoFactsApplication {

    // Inject application name from application.properties
    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(VerrazzanoFactsApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext appContext) {
        return args -> {
            System.out.println("Hello from Verrazzano ...");
            System.out.println("Application Name : " + appName);
            System.out.println("Display Name : " + appContext.getDisplayName());
        };
    }
}
