#!/bin/bash -e

BUILD=`date +I%Y%m%d-%H00`
sed -e s/QUALIFIER=.*/QUALIFIER=$BUILD/ -i local.sh

BUILD_ROOT=$(cd $(dirname $0); pwd)

#$BUILD_ROOT/build-3.3.sh
$BUILD_ROOT/build-3.4.sh

source $BUILD_ROOT/local.sh

pack() {
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.update.core.siteOptimizer \
 -jarProcessor -verbose -processAll -repack -pack \
 -outputDir $1 $1  
}

#pack $BUILD_ROOT/3.3/build/standardUpdateSite
pack $BUILD_ROOT/3.4/build/standardUpdateSite
pack $BUILD_ROOT/3.4/build/extrasUpdateSite
pack $BUILD_ROOT/3.4/build/experimentalUpdateSite
