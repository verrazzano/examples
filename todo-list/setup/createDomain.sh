#!/bin/bash
#
# Copyright (c) 2020, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#

#
# As an alternative to setting up the WebLogic Server domain with the config wizard and WLS console,
# this script creates the on-premise sample domain, deploys the application, and configures the data source
# in the domain using WDT (WebLogic Deploy Tooling).
#

if [ -z "$ORACLE_HOME" ] || [ -z "$WDT_HOME" ] || [ -z "$JAVA_HOME" ]; then
    echo "This script requires that these variables be set: ORACLE_HOME, WDT_HOME, and JAVA_HOME."
    echo "JAVA_HOME   = ${JAVA_HOME}"
    echo "ORACLE_HOME = ${ORACLE_HOME}"
    echo "WDT_HOME    = ${WDT_HOME}"
    exit
fi

# if DOMAIN_HOME is not defined
if [ -z "$DOMAIN_HOME" ]; then
  DOMAIN_HOME=./tododomain
fi

# Remove any existing domain in the domain home folder (in case this is not the first time you ran this script)
echo Removing $DOMAIN_HOME
rm -rf $DOMAIN_HOME

# Run WDT to create a new domain using the model
echo Creating $DOMAIN_HOME
"$WDT_HOME"/bin/createDomain.sh -model_file ./wdt_domain.yaml -archive_file ./wdt_archive.zip -domain_home $DOMAIN_HOME
