#!/bin/sh -e

if [ $# -lt 2 ]
then
  echo "usage: sign-update-site.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/$BUILD
DST=/opt/public/download-staging.priv/tools/mylyn
OUT=$DST/output

/bin/cp -a $SRC/e3.4/mylyn-$MAJOR.$BUILD-e3.4.zip $DST
/bin/cp -a $SRC/extras/mylyn-$MAJOR.$BUILD-extras.zip $DST
/bin/cp -a $SRC/experimental/mylyn-$MAJOR.$BUILD-experimental.zip $DST

/usr/bin/sign mylyn-$MAJOR.$BUILD-e3.4.zip nomail $OUT
/usr/bin/sign mylyn-$MAJOR.$BUILD-extras.zip nomail $OUT
/usr/bin/sign mylyn-$MAJOR.$BUILD-experimental.zip nomail $OUT
