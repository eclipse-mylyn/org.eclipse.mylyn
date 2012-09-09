#!/bin/bash -e

stop() {
TYPE=$1
URL="$2"
DIR=$3
VERSION=$4
CONFIG=$5
PORT=$6

if [ -e $DIR/$TYPE.pid ]
then
 echo Stopping $TYPE $VERSION
 kill $(cat $DIR/$TYPE.pid)
 rm $DIR/$TYPE.pid
else
 echo $TYPE $VERSION is already stopped
fi
}

BASE=$(dirname $0)
source $BASE/../etc/config

for_each stop
