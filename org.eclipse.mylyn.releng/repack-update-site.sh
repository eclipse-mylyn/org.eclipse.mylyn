#!/bin/sh -e

if [ $# -lt 2 ]
then
  echo "usage: repack-update-site.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/$BUILD
DST=/opt/public/download-staging.priv/tools/mylyn
OUT=$DST/output
SITE=$OUT/site
JAVA_HOME=/opt/ibm/java2-ppc-50
ECLIPSE_HOME=/opt/public/download-staging.priv/tools/mylyn/eclipse

pack() {
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.update.core.siteOptimizer \
 -jarProcessor -verbose -processAll -repack -pack \
 -digestBuilder -digestOutputDir=$1 -siteXML=$1/site.xml \
 -outputDir $1 $1
}

repack() {
  mkdir $SITE/$1
  unzip -q -d $SITE/$1 $OUT/mylyn-$MAJOR.$BUILD-$1.zip
  pack $SITE/$1
  cp $OUT/mylyn-$MAJOR.$BUILD-$1.zip $SITE/$1
}

rm -rf $SITE
mkdir $SITE

repack e3.4
repack extras
repack experimental
