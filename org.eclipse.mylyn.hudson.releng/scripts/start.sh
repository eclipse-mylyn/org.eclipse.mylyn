#!/bin/bash -e

start() {
TYPE=$1
URL="$2"
DIR=$3
VERSION=$4
CONFIG=$5
PORT=$6

echo Starting $TYPE $VERSION on port $PORT
HUDSON_HOME=$(cd $DIR; pwd) JENKINS_HOME=$(cd $DIR; pwd) nohup java -Xmx128m -Djava.awt.headless=true -jar $DIR/$TYPE-$VERSION.war --httpPort=$PORT --ajp13Port=-1 --prefix=/$DIR > $DIR/$TYPE.out 2>&1 &
echo $! > $DIR/$TYPE.pid
}

BASE=$(dirname $0)
source $BASE/../etc/config

for_each start
