#!/bin/bash

set -e

BASE=$(dirname $0)

for i in $(find -name site.xml); do
 (
 cd $(dirname $i)
 DIR=$(pwd)
 DIR=${DIR#*/archive/}
 DIR=${DIR%/*}
 $BASE/generate-p2-metadata.sh 
 $BASE/add-mirrors.sh $DIR
 )
done
