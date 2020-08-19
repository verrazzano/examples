#!/bin/bash
#
# Copyright (c) 2020, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#

#
# If you used createDomain.sh to create the WebLogic Server domain, this script can be used to discover
# that domain, and generate the Verrazzano models with WDT.
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

# if OUT_DIR is not defined
if [ -z "$OUT_DIR" ]; then
  OUT_DIR=./v8o
fi

echo Clearing output directory: $OUT_DIR
rm -rf $OUT_DIR
mkdir $OUT_DIR

# Run WDT to discover the on-premises domain generating a WDT model, Verrazzano model, and the binding YAML files.
"$WDT_HOME"/bin/discoverDomain.sh -domain_home $DOMAIN_HOME -model_file $OUT_DIR/wdt-model.yaml -archive_file $OUT_DIR/wdt-archive.zip -target vz -output_dir $OUT_DIR
