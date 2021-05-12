#!/usr/bin/env bash
# Copyright (c) 2021, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

scriptDir="$( cd "$( dirname $0 )" && pwd )"
if [ ! -d ${scriptDir} ]; then
    echo "Unable to determine the sample directory where the application is found"
    echo "Using shell /bin/sh to determine and found ${scriptDir}"
    exit 1
fi

echo ' - metrics exporter...'
rm -rf weblogic-monitoring-exporter
git clone https://github.com/oracle/weblogic-monitoring-exporter
cd weblogic-monitoring-exporter
git checkout v1.1.2
mvn -B clean install
cd webapp
mvn -B clean package -Dconfiguration=../../exporter-config.yaml
cd ../..
mkdir -p ./wlsdeploy/applications
cp weblogic-monitoring-exporter/webapp/target/wls-exporter.war ./wlsdeploy/applications/wls-exporter.war

cp ../LICENSE.txt .
cp ../THIRD_PARTY_LICENSES.txt .

echo 'Build the WDT archive...'
rm wdt_archive.zip
mkdir -p ./wlsdeploy/applications
cp ../target/todo.war ./wlsdeploy/applications
zip -r wdt_archive.zip wlsdeploy
rm -r wlsdeploy

echo 'Download WDT...'
if [ -f weblogic-deploy.zip ]; then
    echo 'Using existing weblogic-deploy.zip...'
else
    echo 'Downloading weblogic-deploy.zip...'
    wget https://github.com/oracle/weblogic-deploy-tooling/releases/download/weblogic-deploy-tooling-1.9.0/weblogic-deploy.zip
fi

echo 'Download WebLogic Image Tool...'
if [ -f imagetool.zip ]; then
    echo 'Using existing imagetool.zip...'
else
    echo 'Downloading imagetool.zip...'
    wget https://github.com/oracle/weblogic-image-tool/releases/download/release-1.9.6/imagetool.zip
    unzip imagetool.zip
fi

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
    --fromImage container-registry.oracle.com/os/oraclelinux:7-slim@sha256:84433cf4f605c35fa032ff87d2635c3ab5aaa7fbdb4bb8f90e60f4ab1b96d371 \
    --wdtModel wdt_domain.yaml \
    --wdtArchive wdt_archive.zip \
    --wdtDomainHome /u01/oracle/user_projects/domains/tododomain \
    --wdtModelOnly \
    --additionalBuildCommands imagetool-additions
