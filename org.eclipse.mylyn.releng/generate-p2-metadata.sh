#!/bin/bash -e

if [ $# -lt 1 ]
then
 ROOT=$HOME/downloads/tools/mylyn/update 
else
 ROOT=$1
fi

JAVA_HOME=/opt/ibm/java2-ppc-50
ECLIPSE_HOME=/shared/tools/mylyn/eclipse

pack() {
DIR=$ROOT/$1
echo Processing $DIR
rm -f $DIR/artifacts.jar $DIR/contents.jar
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator \
 -updateSite $DIR \
 -site file:$DIR/site.xml \
 -metadataRepository file:$DIR \
 -metadataRepositoryName "$2 "\
 -artifactRepository file:$DIR \
 -artifactRepositoryName "$2" \
 -compress \
 -reusePack200Files \
 -noDefaultIUs
}

pack e3.3 "Mylyn for Eclipse 3.3"
pack e3.4 "Mylyn for Eclipse 3.4"
pack extras "Mylyn Extras"
pack incubator "Mylyn Incubator"

echo Done
