#!/bin/bash
#
# Copyright (c) 2020, 2021, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#

docker build --build-arg 'WDT_MODEL=wdt_domain.yaml' --build-arg 'WDT_ARCHIVE=wdt_archive.zip' -t $1 .
