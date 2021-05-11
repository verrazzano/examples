# Copyright (c) 2020, 2021, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/os/oraclelinux:7-slim@sha256:84433cf4f605c35fa032ff87d2635c3ab5aaa7fbdb4bb8f90e60f4ab1b96d371

RUN yum update -y \
    && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum -y install tar gzip \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/lib \
    && mkdir -p /app/conf \
    && mkdir -p /license

ENV JAVA_HOME=/usr/java
ENV PATH $JAVA_HOME/bin:$PATH
ARG JDK_BINARY="${JDK_BINARY:-openjdk-14.0.2_linux-x64_bin.tar.gz}"
COPY ${JDK_BINARY} jdk.tar.gz
ENV JDK_DOWNLOAD_SHA256=91310200f072045dc6cef2c8c23e7e6387b37c46e9de49623ce0fa461a24623d

RUN set -eux \
    echo "Checking JDK hash"; \
    echo "${JDK_DOWNLOAD_SHA256} *jdk.tar.gz" | sha256sum --check -; \
    echo "Installing JDK"; \
    mkdir -p "$JAVA_HOME"; \
    tar xzf jdk.tar.gz --directory "${JAVA_HOME}" --strip-components=1; \
    rm -f jdk.tar.gz;


COPY target/libs/*.jar /app/libs/
COPY target/*.jar /app/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/

CMD java -cp /app/*:/app/libs/* -Dcoherence.tracing.endpoint=zipkin.istio-system -Dcoherence.tracing.ratio=1 org.books.bobby.Main