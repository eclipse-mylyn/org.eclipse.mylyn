#!/bin/bash -e

provision() {
TYPE=$1
URL="$2"
DIR=$3
VERSION=$4
CONFIG=$5
PORT=$6

echo "Provisioning $TYPE $VERSION to $DIR"

mkdir $DIR || true
if [[ "$URL" == *eclipse* ]]
then
 wget -c $URL/$TYPE-$VERSION.war -O $DIR/$TYPE-$VERSION.war
else
 wget -c $URL/$VERSION/$TYPE.war -O $DIR/$TYPE-$VERSION.war
fi
cp -a $BASE/../data/$CONFIG/* $DIR

echo "ProxyPass        /$DIR http://localhost:$PORT/$DIR" >> hudson.conf
echo "ProxyPassReverse /$DIR http://localhost:$PORT/$DIR" >> hudson.conf
}

BASE=$(dirname $0)
source $BASE/../etc/config

rm -f hudson.conf
for_each provision
