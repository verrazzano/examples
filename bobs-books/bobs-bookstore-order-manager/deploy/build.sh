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

# Decode properties in variable file
while IFS=: read -r line; do
  prop=`echo $line | cut -d'=' -f1`
  encodedVal=`echo $line | cut -d'=' -f2-`
  val=`echo $encodedVal | base64 --decode`
  printf '%s\n' "$prop=$val"
done < ${scriptDir}/properties/docker-build/bobs-bookstore-topology.properties.encoded > ${scriptDir}/properties/docker-build/bobs-bookstore-topology.properties

echo 'Do the docker build...'
docker build --no-cache \
    $BUILD_ARG \
    --build-arg WDT_MODEL=bobs-bookstore-topology.yaml \
    --build-arg WDT_ARCHIVE=archive.zip \
    --build-arg ORACLE_HOME=/u01/oracle \
    --build-arg CUSTOM_DOMAIN_NAME=bobs-bookstore \
    --build-arg DOMAIN_PARENT=/u01/oracle \
    --build-arg WDT_VARIABLE_FILE=properties/docker-build/bobs-bookstore-topology.properties \
    --force-rm=true \
    -t $1 .


