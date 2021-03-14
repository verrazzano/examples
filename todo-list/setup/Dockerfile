# Copyright (c) 2020, 2021, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

FROM container-registry.oracle.com/middleware/weblogic:12.2.1.4
#
# We rely on ORACLE_HOME and JAVA_HOME being set by base image
#

USER root

RUN yum update -y && \
        yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true && \
        yum clean all; \
    rm -rf /var/cache/yum

ARG WDT_ARCHIVE
ARG WDT_MODEL

ENV WDT_HOME="/u01/wdt" \
    DOMAINS_DIR="/u01/domains" \
    LICENSE_DIR="/license"

RUN for _i in ${WDT_HOME} ${DOMAINS_DIR} ${LICENSE_DIR} ; \
    do \
        mkdir -p ${_i} && chown oracle:root ${_i} ; \
    done

ADD https://github.com/oracle/weblogic-deploy-tooling/releases/download/release-1.9.8/weblogic-deploy.zip ${WDT_HOME}/

RUN cd ${WDT_HOME} && \
    $JAVA_HOME/bin/jar xf ./weblogic-deploy.zip && \
    rm weblogic-deploy.zip && \
    chmod +xw weblogic-deploy/bin/*.sh && \
    chmod -R +xw weblogic-deploy/lib/python   && \
    chown -R oracle:root weblogic-deploy

COPY ${WDT_MODEL} ${WDT_ARCHIVE} ${WDT_HOME}/models/
RUN chown -R oracle:root ${WDT_HOME}/models

#
# Copy license and third party attribution files when we have them
#
COPY LICENSE.txt ${LICENSE_DIR}/
COPY THIRD_PARTY_LICENSES.txt ${LICENSE_DIR}/
RUN chown -R oracle:root ${LICENSE_DIR}

WORKDIR ${ORACLE_HOME}
USER oracle

CMD ["/bin/bash", "-c", "sleep 600"]]

