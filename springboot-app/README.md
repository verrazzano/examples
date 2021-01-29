# Spring Boot Application
Spring Boot offers a fast way to build applications. It looks at your classpath and at the beans you have configured, makes 
reasonable assumptions about what you are missing, and adds those items. With Spring Boot, you can focus more on business
features and less on infrastructure. More information on building an application with Spring Boot is available at
https://spring.io/guides/gs/spring-boot/

## Requires
- [Maven](https://maven.apache.org/download.cgi) (To build the Spring Boot application)

## Build and deploy the application
The samle web application leverages the Spring Boot Maven plugin, which provides the ability to automatically deploy the
web application in an embedded application server. Please take a look at the following dependency in pom.xml :

    ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId> 
    </dependency>
    ```

When we execute mvn spring-boot:run command from the project root folder, the plugin reads the maven configuration and understands
that the application requires a web container.

    ```
    git clone https://github.com/verrazzano/examples.git
    cd examples/springboot-app
    mvn spring-boot:run
    ```

The application provides few endpoints:  
    1. http://localhost:8080/   // an index page  
    2. http://localhost:8080/facts   // a page displaying random verrazzano facts  
    3. http://localhost:8080/actuator  // actuator endpoint  
    4. http://localhost:8080/actuator/prometheus   // prometheus endpoint  

## Create a Docker image
The Dockerfile provided in this example uses Oracle Linux image as the base image, which doesn't include the Java Development Kit (JDK).
The Dockerfile expects openjdk-11_linux-x64_bin.tar.gz in the project root directory, which is available in [OpenJDK General-Availability Releases] (https://jdk.java.net/archive/) page.

    ```
    cd examples/springboot-app
    mvn package
    docker build -t example/spring-boot-application .
    ```

## Copyright
Copyright (c) 2020, 2021, Oracle and/or its affiliates.
