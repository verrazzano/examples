#!/usr/bin/env bash
# Copyright (c) 2020, 2023, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

TAG=${TAG:-test}
CONTAINER_RUNTIME="${CONTAINER_RUNTIME:-docker}"

mvn clean install
"${CONTAINER_RUNTIME}" build --force-rm=true -f Dockerfile -t ghcr.io/verrazzano/example-bobbys-helidon-stock-application:${TAG} .