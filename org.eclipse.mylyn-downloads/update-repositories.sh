#!/bin/bash

set -e

BASEDIR=$(cd $(dirname $0) && pwd)
TARGET=/home/data/httpd/download.eclipse.org/mylyn

cp -r snapshots releases $TARGET

cd $TARGET
$BASEDIR/update-composite.sh -r
