#!/bin/bash

set -e

BASE=$(dirname $0)

for i in $(find -name site.xml); do
 (
 cd $(dirname $i)
 DIR=$(pwd)
 DIR=${DIR##*drops/}
 DIR=${DIR%%/*}
 if [ -z "$DIR" ]; then
   echo "Failed to determine version for $i. Exiting."
   exit 1
 fi
 echo Version: $DIR 
 #$BASE/generate-p2-metadata.sh 
 $BASE/add-mirrors.sh $DIR
 echo 
 )
done
