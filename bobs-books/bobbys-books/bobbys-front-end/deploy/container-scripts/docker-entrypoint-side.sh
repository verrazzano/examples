#!/bin/bash
#
# Copyright (c) 2022, Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
#
# sidecar用 メインスクリプト
#
# このシェルは adminserver と managed-server の両方で実行されるので注意。
#

set -x

echo "docker-entrypoint-side.sh Enter"

ADMIN_PORT=${ADMIN_PORT:-26450}

echo "whoami=$( whoami )"

echo "display ipc shared memory list"
ipcs -m

# ポート7001がLISTGENになる = WebLogic Server (adminserver) 起動するまで待機
/u01/oracle/waitForListen.sh $ADMIN_PORT pwd

# post.d の中のシェルを順番に実行
RCALL=0
FAIL_LIST=
for f in /u01/oracle/post.d/*.sh
do
    if [ -f "$f" ] ; then
        echo "starting ... $f"
        bash -x $f
        RC=$?
        if [ $RC -ne 0 ] ; then
            echo "ERROR: $f"
            RCALL=$RC
            FAIL_LIST="$FAIL_LIST $f"
        fi
    fi
done

# 実行完了
if [ $RCALL -eq 0 ] ; then
    echo "docker-entrypoint-side.sh SUCCESS"
else
    echo "docker-entrypoint-side.sh FAILURE"
    echo "docker-entrypoint-side.sh FAILURE LIST $FAIL_LIST"
fi

# このシェルはコンテナのメインプロセスなので終了してはいけない。ここで無限に待機する
tail -f /dev/null
