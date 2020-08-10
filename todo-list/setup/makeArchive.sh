#!/bin/bash

rm wdt_archive.zip
mkdir -p ./wlsdeploy/applications
cp ../target/todo.war ./wlsdeploy/applications
zip -r wdt_archive.zip wlsdeploy
rm -r wlsdeploy