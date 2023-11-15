# Copyright (c) 2020, 2021, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/os/oraclelinux:7-slim@sha256:84433cf4f605c35fa032ff87d2635c3ab5aaa7fbdb4bb8f90e60f4ab1b96d371

RUN yum update -y \
    && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum -y install tar gzip \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/libs \
    && mkdir -p /app/classpath \
    && mkdir -p /license

ENV JAVA_HOME=/usr/java
ENV PATH $JAVA_HOME/bin:$PATH
ARG JAVA_URL="${JAVA_URL:-https://download.java.net/java/GA/jdk21.0.1/415e3f918a1f4062a0074a2794853d0d/12/GPL}"

RUN set -eux; \
    ARCH="$(uname -m)" && \
    # Java uses just x64 in the name of the tarball
    if [ "$ARCH" = "x86_64" ]; \
        then ARCH="x64"; \
    fi && \
    JAVA_PKG="$JAVA_URL"/openjdk-21.0.1_linux-"${ARCH}"_bin.tar.gz ; \
    JAVA_SHA256="$(curl "$JAVA_PKG".sha256)" ; \
    curl --output jdk.tgz "$JAVA_PKG" && \
    echo "Checking JDK hash"; \
    echo "$JAVA_SHA256" jdk.tgz | sha256sum --check - && \
    echo "Installing JDK"; \
    mkdir -p "$JAVA_HOME" && \
    tar xzf jdk.tgz --directory "${JAVA_HOME}" --strip-components=1; \
    rm -f jdk.tgz;

COPY target/libs/*.jar /app/libs/
COPY target/*.jar /app/classpath/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/

USER 1000

