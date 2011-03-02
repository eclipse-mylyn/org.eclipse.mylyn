#!/bin/sh -e

SRC=https://hudson.eclipse.org/hudson/job/mylyn-release/lastSuccessfulBuild/artifact/org.eclipse.mylyn/org.eclipse.mylyn.releng/main-site/target

wget -O version.properties $SRC/version.properties

VERSION=$(head -n 1 version.properties)
QUALIFIER=$(tail -n 1 version.properties)
SRC=$2
DST=/home/data/httpd/download.eclipse.org/mylyn/archive/$VERSION/$QUALIFIER

SITE=/home/data/httpd/download.eclipse.org/mylyn/snapshots

rm version.properties

if [ -e $DST ]; then
 echo $DST already exists
 exit 1
fi

echo Promoting $VERSION.$QUALIFIER

mkdir -p $DST/
unzip -d $DST/ $SRC/site-packed.zip 
cp $SRC/site-archive.zip $DST/mylyn-$VERSION.$QUALIFIER.zip

#chgrp -R mylynadmin $DST
#chmod g+w -R $DST

echo Updating $SITE
cd $(dirname $0)
BASE=$(pwd)

for i in $SITE/*; do
 echo "Updating $i"
 cd $i
 $BASE/update-composite.sh
done
