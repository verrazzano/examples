#!/bin/bash
# Copyright (c) 2020 Oracle and/or its affiliates.

cd src/main/web
npm install
cd ../../../
mvn clean install
docker build --force-rm=true -f Dockerfile -t docker.pkg.github.com/verrazzano/demo-apps/roberts-helidon-stock-application:0.1.0 .