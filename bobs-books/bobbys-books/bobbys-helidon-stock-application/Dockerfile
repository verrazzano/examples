# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM openjdk:11-oracle
RUN yum update -y && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum clean all; rm -rf /var/cache/yum && mkdir -p /license

ENV COH_WKA bobbys-coherence-wka
ENV COH_CLUSTER bobbys-coherence
ENV COH_CACHE_CONFIG bobbys-cache-config.xml
ENV COH_POF_CONFIG bobbys-pof-config.xml

RUN mkdir /app
COPY target/* /app/
COPY LICENSE.txt /license/
COPY THIRD_PARTY_LICENSES.txt /license/

CMD java -cp /app/bobbys-helidon-stock-application.jar:/app/coherence-12.2.1-4-0.jar:/app/* -Dcoherence.distributed.localstorage=true -Dcoherence.wka=$COH_WKA -Dcoherence.cluster=$COH_CLUSTER -Dcoherence.cacheconfig=$COH_CACHE_CONFIG -Dcoherence.pof.config=$COH_POF_CONFIG -Dcoherence.tracing.endpoint=zipkin.istio-system -Dcoherence.tracing.ratio=1 org.books.bobby.Main
