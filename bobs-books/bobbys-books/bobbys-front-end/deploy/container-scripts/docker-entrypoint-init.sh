#!/bin/bash
#
# initContainers用 メインスクリプト
#
# このシェルは adminserver と managed-server の両方で実行されるので注意。
#

set -x -e

echo "docker-entrypoint-init.sh ENTER"

echo "whoami=$( whoami )"

# コンテナ間で共有するディレクトリの準備 /home/docomo, /home/docomo2, /home/LOG は共有する。 mkdir の内容は docker-entrypoint-init.sh の中と additional-dockerfile の中で 一致していること。
mkdir -p /home/LOG/DSP \
             /home/LOG/WebLogic/jfr/repository \
             /home/docomo \
             /home/docomo/etc \
             /home/docomo/kyoutu \
             /home/docomo/tmp \
             /home/docomo2 \
             /home/dsp/dsp-link \
             /server-volume/home/LOG/WebLogic/jfr/repository && \
             chown -R oracle:root /home/docomo /home/docomo2 /home/LOG /server-volume/home/LOG

# イメージ内の資産を /home に展開
( cd /home.orig ; tar cpf - . ) | tar xpf - -C /home
chown -R oracle:root /home/docomo /home/docomo2 /home/LOG

# ホスト名表示
hostname

# 設定ファイル書き換え用環境変数設定
export DUMMYHOST=$(hostname)

# Pod起動後のテンプレート適用とコピー
bash -ex /u01/oracle/applyEnvsubst.sh
tree -l /template-output
tree -l /home
cp -r /template-output/home /
chown -R oracle:root /home/docomo /home/docomo2 /home/LOG

# 資産ファイルの属性変更
# if [ -f /file-attrs.txt ] ; then cat /file-attrs.txt | bash /u01/oracle/loadFileAttributes.sh --directory / ; fi

# 資産ファイルの属性確認
# bash -e -x /u01/oracle/saveFileAttributes.sh --directory /home/docomo2

echo "create ipc shared memory"
ipcmk -M 100K

echo "display ipc shared memory list"
ipcs -m

# domain作成後、Pod起動前の設定処理。フォアグラウンド実行。
for f in /u01/oracle/pre.d/*.sh
do
    if [ -f "$f" ] ; then
        echo "start $f"
        bash -x $f
        RC=$?
        if [ $RC -eq 0 ] ; then
            echo "success $f"
        else
            echo "failure $f"
        fi
    fi
done

echo "docker-entrypoint-init.sh SUCCESS"
