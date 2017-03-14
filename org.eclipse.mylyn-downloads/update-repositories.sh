#!/bin/bash

set -e

BASEDIR=$(cd $(dirname $0) && pwd)
TARGET=/home/data/httpd/download.eclipse.org/mylyn

cp -R $BASEDIR/snapshots $BASEDIR/releases $TARGET

cd $TARGET
$BASEDIR/update-composite.sh -r

cd $TARGET
for i in $(find -name composite.index); do
  for j in $(dirname $i)/*.xml $(dirname $i)/*.index; do
    [ -O $j ] && chgrp mylyn $j || true
    [ -O $j ] && chmod g+rw $j || true 
  done
done
