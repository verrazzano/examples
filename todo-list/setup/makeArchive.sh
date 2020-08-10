#!/bin/bash
#
# Copyright (c) 2020, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#

rm wdt_archive.zip
mkdir -p ./wlsdeploy/applications
cp ../target/todo.war ./wlsdeploy/applications
zip -r wdt_archive.zip wlsdeploy
rm -r wlsdeploy