#!/bin/sh -e


wget -O version.properties https://hudson.eclipse.org/hudson/job/mylyn-release/lastSuccessfulBuild/artifact/org.eclipse.mylyn/org.eclipse.mylyn.releng/main-site/target/version.properties

VERSION=$(head -n 1 version.properties)
QUALIFIER=$(tail -n 1 version.properties)
SRC=https://hudson.eclipse.org/hudson/job/mylyn-release/lastSuccessfulBuild/artifact/org.eclipse.mylyn/org.eclipse.mylyn.releng/main-site/target
DST=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$VERSION/$QUALIFIER

SITE=/home/data/httpd/download.eclipse.org/tools/mylyn/update/weekly

rm version.properties

if [ -e $DST ]; then
 echo $DST already exists
 exit 1
fi

echo Downloading $VERSION.$QUALIFIER

wget -O site.zip $SRC/site-packed.zip
unzip -d  site.zip 
wget wget -O $DST/mylyn-$VERSION.$QUALIFIER.zip $SRC/site-archive.zip
rm site.zip

echo Updating $SITE
$(dirname $0)/create-composite.sh $SITE
