#!/bin/bash
# Copyright (c) 2020, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

retries=0

echo 'Checking if port 6060 is in use...'

until [ "$retries" -ge 10 ]
do 
  port_in_use=$(netstat -tulpn 2>/dev/null|grep 6060|wc -l)
  if [ "$port_in_use" == "0" ]; then
    break;
  fi
  retries=$(($retries+1))
  sleep 5
done
if [ "$retries" -ge 10 ]; then
  echo 'Port 6060 was in use, gave up'
  exit 1
fi

echo 'Port is free'
