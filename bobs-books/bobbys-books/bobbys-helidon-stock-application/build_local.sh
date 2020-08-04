#!/bin/bash
# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

mvn clean install
docker build --force-rm=true -f Dockerfile -t docker.pkg.github.com/verrazzano/examples/bobbys-helidon-stock-application:0.1.10-1 .