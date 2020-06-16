#!/bin/bash
# Copyright (c) 2020, Oracle Corporation and/or its affiliates.

mvn clean install
docker build --force-rm=true -t docker.pkg.github.com/verrazzano/demo-apps/bobbys-coherence:0.1.0 .