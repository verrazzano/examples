#!/bin/bash
# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

mvn clean install
docker build -t docker.pkg.github.com/verrazzano/examples/roberts-coherence:0.1.10-1 .