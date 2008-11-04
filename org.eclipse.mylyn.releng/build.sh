#!/bin/bash -e

if [ "$1" == "-rebuild" ];
then
 shift
else
 BUILD=`date -u +I%Y%m%d-%H00`
 sed -i~ -e s/QUALIFIER=.*/QUALIFIER=$BUILD/ local.sh
fi

BUILD_ROOT=$(cd $(dirname $0); pwd)

chmod 755 $BUILD_ROOT/*.sh

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

$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator \
 -updateSite $1 \
 -site file:$1/site.xml \
 -metadataRepository file:$1 \
 -metadataRepositoryName "$2" \
 -artifactRepository file:$1 \
 -artifactRepositoryName "$2" \
 -compress \
 -reusePack200Files \
 -noDefaultIUs
}

pack $BUILD_ROOT/3.3/build/standardUpdateSite "Mylyn Weekly for Eclipse 3.3"
pack $BUILD_ROOT/3.4/build/standardUpdateSite "Mylyn Weekly for Eclipse 3.4"
pack $BUILD_ROOT/3.4/build/extrasUpdateSite "Mylyn Weekly Extras"
pack $BUILD_ROOT/3.4/build/incubatorUpdateSite "Mylyn Weekly Incubator"
