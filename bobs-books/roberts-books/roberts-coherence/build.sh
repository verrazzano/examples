#!/bin/bash
# Copyright (c) 2020, Oracle Corporation and/or its affiliates.

mvn clean install
docker build -t docker.pkg.github.com/verrazzano/examples/roberts-coherence:0.1.0 .