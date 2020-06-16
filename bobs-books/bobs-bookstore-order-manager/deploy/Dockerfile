# Copyright (c) 2020, Oracle Corporation and/or its affiliates.

FROM store/oracle/weblogic:12.2.1.3
USER root
RUN yum update -y && yum-config-manager --save --setopt=ol7_ociyum_config.skip_if_unavailable=true \
    && yum clean all; rm -rf /var/cache/yum 

# Switching the User back to oracle after the yum update.
USER oracle

ARG WDT_ARCHIVE
ARG WDT_MODEL
ARG WDT_VARIABLE_FILE
ARG CUSTOM_ADMIN_NAME=admin-server
ARG CUSTOM_ADMIN_HOST=wlsadmin
ARG CUSTOM_ADMIN_PORT=7001
ARG CUSTOM_MANAGED_SERVER_PORT=8001
ARG CUSTOM_DOMAIN_NAME=bobs-bookstore
ARG CUSTOM_DEBUG_PORT=8453

# Persist arguments - for ports to expose and container to use
# Create a placeholder for the manager server name. This will be provided when run the container
# Weblogic and Domain locations
# Set the start derby in admin server flag to true. Override this in the runtime properties
# The boot.properties will be created under the DOMAIN_HOME when the admin server container is run
# WDT installation
# ---------------------------
ENV ADMIN_NAME=${CUSTOM_ADMIN_NAME} \
    ADMIN_HOST=${CUSTOM_ADMIN_HOST} \
    ADMIN_PORT=${CUSTOM_ADMIN_PORT} \
    MANAGED_SERVER_NAME=${MANAGED_SERVER_NAME} \
    MANAGED_SERVER_PORT=${CUSTOM_MANAGED_SERVER_PORT} \
    DEBUG_PORT=${CUSTOM_DEBUG_PORT} \
    DERBY_FLAG="true" \
    ORACLE_HOME=/u01/oracle \
    DOMAIN_NAME=${CUSTOM_DOMAIN_NAME} \
    DOMAIN_PARENT=${ORACLE_HOME}/user_projects/domains

ENV DOMAIN_HOME=${DOMAIN_PARENT}/${DOMAIN_NAME} \
    PROPERTIES_FILE_DIR=$ORACLE_HOME/properties \
    WDT_HOME="/u01" \
    SCRIPT_HOME="${ORACLE_HOME}" \
    PATH=$PATH:${ORACLE_HOME}/oracle_common/common/bin:${ORACLE_HOME}/wlserver/common/bin:${DOMAIN_HOME}:${DOMAIN_HOME}/bin:${ORACLE_HOME}

COPY weblogic-deploy.zip ${WDT_HOME}
COPY container-scripts/* ${SCRIPT_HOME}/

# Create the properties file directory and the domain home parent with the correct permissions / owner.
# Unzip and install the WDT image and change the permissions / owner.
USER root
RUN chmod +xw $SCRIPT_HOME/*.sh && \
    chown -R oracle:oracle ${SCRIPT_HOME} && \
    mkdir -p +xwr $PROPERTIES_FILE_DIR && \
    chown -R oracle:oracle $PROPERTIES_FILE_DIR && \
    mkdir -p $DOMAIN_PARENT && \
    chown -R oracle:oracle $DOMAIN_PARENT && \
    chmod -R a+xwr $DOMAIN_PARENT && \
    cd ${WDT_HOME} && \
    $JAVA_HOME/bin/jar xf ./weblogic-deploy.zip && \
    rm weblogic-deploy.zip && \
    chmod +xw weblogic-deploy/bin/*.sh && \
    chmod -R +xw weblogic-deploy/lib/python   && \
    chown -R oracle:oracle weblogic-deploy

# Persist the WDT tool home location
ENV WDT_HOME=$WDT_HOME/weblogic-deploy

# Copy the WDT model, archive file, and credential secrets to the property file directory.
# These files will be removed after the image is built.
# Be sure to build with --force-rm to eliminate this container layer

COPY ${WDT_MODEL} ${WDT_ARCHIVE} properties/docker-build/*.properties ${PROPERTIES_FILE_DIR}/
# --chown for COPY is available in docker version 18 'COPY --chown oracle:oracle'
RUN chown -R oracle:oracle ${PROPERTIES_FILE_DIR}

# Create the domain home in the docker image.
#
# The create domain tool creates a domain at the DOMAIN_HOME location
# The domain name is set using the value in the model / variable files
# The domain name can be different from the DOMAIN_HOME domain folder name.
#
# Set WORKDIR for @@PWD@@ global token in model file
WORKDIR $ORACLE_HOME
USER oracle
RUN if [ -n "$WDT_MODEL" ]; then MODEL_OPT="-model_file $PROPERTIES_FILE_DIR/${WDT_MODEL##*/}"; fi && \
    if [ -n "$WDT_VARIABLE_FILE" ]; then VARIABLE_OPT="-variable_file $PROPERTIES_FILE_DIR/${WDT_VARIABLE_FILE##*/}"; fi && \
    ${WDT_HOME}/bin/createDomain.sh \
        -oracle_home $ORACLE_HOME \
        -java_home $JAVA_HOME \
        -domain_home $DOMAIN_HOME \
        -domain_type WLS \
        $MODEL_OPT \
        $VARIABLE_OPT \
        -archive_file ${PROPERTIES_FILE_DIR}/${WDT_ARCHIVE} && \
    chmod -R a+x $DOMAIN_HOME/bin/*.sh && \
    rm -rf $PROPERTIES_FILE_DIR && \
    rm -rf $DOMAIN_HOME/lib/mysql-connector-java-8.0.13.jar && \
    rm -rf $ORACLE_HOME/inventory/featuresets/resources/modules/mysql_jdbc_5.1.22.0.1.jar && \
    rm -rf $ORACLE_HOME/oracle_common/modules/mysql-connector-java-commercial-5.1.22/mysql-connector-java-commercial-5.1.22-bin.jar


COPY WebLogicLoggingExporter.yaml $DOMAIN_HOME/config/WebLogicLoggingExporter.yaml

# Mount the domain home and the WDT home for easy access.
VOLUME $DOMAIN_HOME
VOLUME $WDT_HOME

# Expose admin server, managed server port and domain debug port
EXPOSE $ADMIN_PORT $MANAGED_SERVER_PORT $DEBUG_PORT

WORKDIR $DOMAIN_HOME

# Define default command to start Admin Server in a container.
CMD ["/u01/oracle/startAdminServer.sh"]
