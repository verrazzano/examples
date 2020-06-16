# Copyright (c) 2020, Oracle Corporation and/or its affiliates.

FROM oraclelinux:7-slim

RUN yum update -y && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum clean all; rm -rf /var/cache/yum \
    && mkdir -p /app/lib \
    && mkdir -p /app/conf

COPY target/*.jar /app/lib/
COPY src/main/resources/books-cache-config.xml /app/conf/
COPY src/main/resources/books-pof-config.xml /app/conf/
COPY src/main/resources/books.csv /app/conf/
