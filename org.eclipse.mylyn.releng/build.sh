#!/bin/bash -e

if [ "$1" == "-rebuild" ];
then
 shift
else
 BUILD=`date -u +I%Y%m%d-%H00`
 sed -e s/QUALIFIER=.*/QUALIFIER=$BUILD/ -i local.sh
fi

BUILD_ROOT=$(cd $(dirname $0); pwd)

rm -R $BUILD_ROOT/3.3/build || true
rm -R $BUILD_ROOT/3.4/build || true

$BUILD_ROOT/build-3.3.sh $*
$BUILD_ROOT/build-3.4.sh $*

source $BUILD_ROOT/local.sh

pack() {
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.update.core.siteOptimizer \
 -jarProcessor -verbose -processAll -repack -pack \
 -outputDir $1 $1  
}

pack $BUILD_ROOT/3.3/build/standardUpdateSite
pack $BUILD_ROOT/3.4/build/standardUpdateSite
pack $BUILD_ROOT/3.4/build/extrasUpdateSite
pack $BUILD_ROOT/3.4/build/incubatorUpdateSite
