#!/bin/bash -e

if [ $# -lt 2 ]
then
  echo "usage: create-api-profile.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/$BUILD
OUT=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/mylyn-$MAJOR-api.zip
TMP=/shared/tools/mylyn/tmp/profile

rm -rf $TMP || true
mkdir $TMP

/usr/bin/find $SRC -name "org.eclipse.mylyn*.jar" | zip $DST/mylyn.zip -D -@
