#!/bin/sh -e

VERSION=$1
QUALIFIER=$2
SRC=$3
DST=/home/data/httpd/download.eclipse.org/mylyn/archive/$VERSION/$QUALIFIER
SITE=/home/data/httpd/download.eclipse.org/mylyn/snapshots

if [ -e $DST ]; then
 echo $DST already exists
 exit 1
fi

echo Promoting $VERSION.$QUALIFIER

mkdir -p $DST/
unzip -d $DST/ $SRC/site-packed.zip 
cp $SRC/site-archive.zip $DST/mylyn-$VERSION.$QUALIFIER.zip

#chgrp -R mylynadmin $DST
chmod g+w -R $DST

echo Updating $SITE
cd $(dirname $0)
BASE=$(pwd)

for i in $SITE/*; do
 echo "Updating $i"
 cd $i
 $BASE/update-composite.sh
done
