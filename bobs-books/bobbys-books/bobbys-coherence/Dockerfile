# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/os/oraclelinux:7-slim

RUN yum update -y && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/lib \
    && mkdir -p /app/conf \
    && mkdir -p /license

ENV JAVA_HOME=/usr/java
ENV PATH $JAVA_HOME/bin:$PATH
ARG GRAALVM_BINARY="${GRAALVM_BINARY:-graalvm-ee-java11-linux-amd64-20.1.0.1.tar.gz}"
COPY ${GRAALVM_BINARY} graalvm.tar.gz
ENV GRAALVM_DOWNLOAD_SHA256=870e51d13e7f42df50097110b14715e765e2a726aa2609c099872995c4409d8f

RUN set -eux \
    echo "Checking GraalVM hash"; \
    echo "${GRAALVM_DOWNLOAD_SHA256} *graalvm.tar.gz" | sha256sum --check -; \
    echo "Installing GraalVM"; \
    mkdir -p "$JAVA_HOME"; \
    tar xzf graalvm.tar.gz --directory "${JAVA_HOME}" --strip-components=1; \
    rm -f graalvm.tar.gz; \
    rm -Rf "$JAVA_HOME/include" "$JAVA_HOME/jmods" "$JAVA_HOME/languages" "$JAVA_HOME/tools" "$JAVA_HOME/lib/svm" "$JAVA_HOME/lib/installer" "$JAVA_HOME/lib/visualvm" "$JAVA_HOME/lib/truffle" "$JAVA_HOME/lib/polyglot"; \
    rm -f "$JAVA_HOME/lib/src.zip" "$JAVA_HOME/lib/libjvmcicompiler.so" "$JAVA_HOME/bin/polyglot"; \
    java -Xshare:dump

COPY target/*.jar /app/lib/
COPY src/main/resources/bobbys-cache-config.xml /app/conf/
COPY src/main/resources/bobbys-pof-config.xml /app/conf/
COPY src/main/resources/books.csv /app/conf/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/
