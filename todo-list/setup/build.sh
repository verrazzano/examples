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

./createArchive.sh
./createDomainImage.sh $1
