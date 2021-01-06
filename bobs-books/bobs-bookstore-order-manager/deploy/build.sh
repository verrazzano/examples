#!/usr/bin/env bash
# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

scriptDir="$( cd "$( dirname $0 )" && pwd )"
if [ ! -d ${scriptDir} ]; then
    echo "Unable to determine the sample directory where the application is found"
    echo "Using shell /bin/sh to determine and found ${scriptDir}"
    exit 1
fi

echo 'Make the archive...'
rm -Rf ${scriptDir}/archive
mkdir -p ${scriptDir}/archive/wlsdeploy/apps
mkdir -p ${scriptDir}/archive/wlsdeploy/domainLibraries
cp ../target/bobs-bookstore-order-manager.war archive/wlsdeploy/apps
cp $HOME/.m2/repository/mysql/mysql-connector-java/8.0.20/mysql-connector-java-8.0.20.jar \
   archive/wlsdeploy/domainLibraries/mysql-connector-java-commercial-8.0.20.jar
cp ../target/bobs-bookstore-order-manager/WEB-INF/lib/opentracing-jdbc-0.2.2.jar archive/wlsdeploy/domainLibraries
cp ../target/bobs-bookstore-order-manager/WEB-INF/lib/opentracing-noop-0.33.0.jar archive/wlsdeploy/domainLibraries
cp ../target/bobs-bookstore-order-manager/WEB-INF/lib/opentracing-util-0.33.0.jar archive/wlsdeploy/domainLibraries
cp ../target/bobs-bookstore-order-manager/WEB-INF/lib/opentracing-api-0.33.0.jar archive/wlsdeploy/domainLibraries

cd archive
jar cvf ../archive.zip *
cd ..

echo ' - metrics exporter...'
rm -rf weblogic-monitoring-exporter
git clone https://github.com/oracle/weblogic-monitoring-exporter
cd weblogic-monitoring-exporter
git checkout v1.1.2
mvn -B clean install
cd webapp
mvn -B clean package -Dconfiguration=../../exporter-config.yaml
cd ../..
cp weblogic-monitoring-exporter/webapp/target/wls-exporter.war \
   ${scriptDir}/archive/wlsdeploy/apps/wls-exporter.war

echo 'Build the WDT archive...'
rm archive.zip
${JAVA_HOME}/bin/jar cvf ${scriptDir}/archive.zip  -C ${scriptDir}/archive wlsdeploy

echo 'Download WDT...'
if [ -f weblogic-deploy.zip ]; then
    echo 'Using existing weblogic-deploy.zip...'
else
    echo 'Downloading weblogic-deploy.zip...'
    wget https://github.com/oracle/weblogic-deploy-tooling/releases/download/weblogic-deploy-tooling-1.9.0/weblogic-deploy.zip
fi

cp ../LICENSE.txt .
cp ../THIRD_PARTY_LICENSES.txt .

echo 'Skipping download of WebLogic Image Tool - using one built from a branch with fixes we need...'
unzip imagetool.zip

# echo 'Download WebLogic Image Tool...'
# if [ -f imagetool.zip ]; then
#     echo 'Using existing imagetool.zip...'
# else
#     echo 'Downloading imagetool.zip...'
#     wget https://github.com/oracle/weblogic-image-tool/releases/download/release-1.9.1/imagetool.zip
#     unzip imagetool.zip
# fi

export PATH=`pwd`/imagetool/bin:$PATH

echo 'Add installers to Image Tool cache...'
imagetool.sh cache addInstaller --type jdk --version 8u261 --path ${JDK8_BUNDLE}
imagetool.sh cache addInstaller --type wls --version 12.2.1.4.0 --path ${WEBLOGIC_BUNDLE}
imagetool.sh cache addInstaller --type wdt --version latest --path weblogic-deploy.zip

echo 'Create image with domain...'
imagetool.sh create \
    --tag $1 \
    --version 12.2.1.4.0 \
    --jdkVersion 8u261 \
    --fromImage container-registry.oracle.com/os/oraclelinux:7-slim@sha256:fcc6f54bb01fc83319990bf5fa1b79f1dec93cbb87db3c5a8884a5a44148e7bb \
    --wdtModel bobs-bookstore-topology.yaml \
    --wdtArchive archive.zip \
    --wdtVariables properties/docker-build/bobs-bookstore-topology.properties \
    --wdtDomainHome /u01/oracle/user_projects/domains/bobs-bookstore \
    --wdtModelOnly \
    --additionalBuildCommands imagetool-additions

