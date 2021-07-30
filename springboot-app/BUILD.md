# Building the Spring Boot Application
Spring Boot offers a fast way to build applications. It examines your classpath and the beans you have configured, makes
reasonable assumptions about what is missing, and adds those items. With Spring Boot, you can focus more on business
features and less on infrastructure. For more information on building an application with Spring Boot, see
https://spring.io/guides/gs/spring-boot/.

## Requires

[Maven](https://maven.apache.org/download.cgi) (To build the Spring Boot application)

## Build and deploy the application
The sample web application uses the Spring Boot Maven plug-in, which provides the ability to automatically deploy the
web application in an embedded application server. Look at the following dependency in the `pom.xml` file :

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

When you run the `mvn spring-boot:run` command from the project root folder, the plug-in reads the Maven configuration and understands
that the application requires a web container.

    $ git clone https://github.com/verrazzano/examples.git
    $ cd examples/springboot-app
    $ mvn spring-boot:run

The application provides a few endpoints:  
* http://localhost:8080/   // An index page  
* http://localhost:8080/facts   // A page displaying random Verrazzano facts  
* http://localhost:8080/actuator  // Spring Boot actuator endpoint  
* http://localhost:8080/actuator/prometheus   // Prometheus endpoint  

## Create a Docker image
The Dockerfile provided in this example uses an Oracle Linux image as the base image, which doesn't include the Java Development Kit (JDK).
The Dockerfile expects `openjdk-11_linux-x64_bin.tar.gz` in the project root directory, which is available on the [OpenJDK General-Availability Releases](https://jdk.java.net/archive/) page.

    $ cd examples/springboot-app
    $ mvn package
    $ docker build -t example/spring-boot-application .


Copyright (c) 2020, 2021, Oracle and/or its affiliates.
