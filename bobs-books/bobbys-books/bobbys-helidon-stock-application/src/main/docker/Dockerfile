# Copyright (c) 2020, Oracle and/or its affiliates.

FROM openjdk:8-jre-slim

RUN mkdir /app
COPY libs /app/libs
COPY ${project.artifactId}.jar /app

CMD ["java", "-jar", "/app/${project.artifactId}.jar"]
