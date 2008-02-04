#!/bin/bash -e

BUILD=`date +I%Y%m%d-%H00`
sed -e s/QUALIFIER=.*/QUALIFIER=$BUILD/ -i local.sh

BUILD_ROOT=$(cd $(dirname $0); pwd)

$BUILD_ROOT/build-3.3.sh
$BUILD_ROOT/build-3.4.sh
