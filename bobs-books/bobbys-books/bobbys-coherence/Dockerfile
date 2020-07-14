# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/os/oraclelinux:7-slim

RUN yum update -y && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/lib \
    && mkdir -p /app/conf \
    && mkdir -p /license

COPY target/*.jar /app/lib/
COPY src/main/resources/bobbys-cache-config.xml /app/conf/
COPY src/main/resources/bobbys-pof-config.xml /app/conf/
COPY src/main/resources/books.csv /app/conf/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/
