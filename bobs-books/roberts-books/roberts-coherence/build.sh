#!/bin/bash
# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

TAG=${1:-test}

mvn clean install
docker build -t ghcr.io/verrazzano/example-roberts-coherence:${TAG} .