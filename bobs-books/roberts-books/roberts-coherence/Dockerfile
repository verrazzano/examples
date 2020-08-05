# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/os/oraclelinux:7-slim@sha256:9b86d1332a883ee8f68dd44ba42133de518b2e0ec1cc70257e59fb4da86b1ad3

RUN yum update -y \
    && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum -y install tar gzip \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/lib \
    && mkdir -p /app/conf \
    && mkdir -p /license

ENV JAVA_HOME=/usr/java
ENV PATH $JAVA_HOME/bin:$PATH
ARG JDK_BINARY="${JDK_BINARY:-jdk-8u261-linux-x64.tar.gz}"
COPY ${JDK_BINARY} jdk.tar.gz
ENV JDK_DOWNLOAD_SHA256=5a04e01a091f6b1ed9c0b801be4fd10689af07eeb9e27f012c9aa3af9948ea34

RUN set -eux \
    echo "Checking JDK hash"; \
    echo "${JDK_DOWNLOAD_SHA256} *jdk.tar.gz" | sha256sum --check -; \
    echo "Installing JDK"; \
    mkdir -p "$JAVA_HOME"; \
    tar xzf jdk.tar.gz --directory "${JAVA_HOME}" --strip-components=1; \
    rm -f jdk.tar.gz;

COPY target/*.jar /app/lib/
COPY src/main/resources/books-cache-config.xml /app/conf/
COPY src/main/resources/books-pof-config.xml /app/conf/
COPY src/main/resources/books.csv /app/conf/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/