#!/bin/sh -e

SRC=https://hudson.eclipse.org/hudson/job/mylyn-incubator-nightly/lastSuccessfulBuild/artifact/org.eclipse.mylyn.incubator-site/target

wget -O version.properties $SRC/version.properties

VERSION=$(head -n 1 version.properties)
QUALIFIER=$(tail -n 1 version.properties)
MAJOR=3.5
DST=/home/data/httpd/download.eclipse.org/mylyn/incubator/$QUALIFIER
SITE=/home/data/httpd/download.eclipse.org/mylyn/incubator/$MAJOR

rm version.properties

if [ -e $DST ]; then
 echo $DST already exists
 exit 1
fi

echo Downloading $VERSION.$QUALIFIER

wget -O site.zip $SRC/site-packed.zip
mkdir -p $DST/
unzip -d $DST/ site.zip 
wget -O $DST/mylyn-incubator-$QUALIFIER.zip $SRC/site-archive.zip
rm site.zip

chgrp -R mylynadmin $DST
chmod g+w -R $DST

echo Updating $SITE
cd $(dirname $0)
BASE=$(pwd)

cd $SITE
$BASE/create-composite.sh

