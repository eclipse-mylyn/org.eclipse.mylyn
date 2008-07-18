#!/bin/bash -e

ROOT=$HOME/downloads/tools/mylyn/update
JAVA_HOME=/opt/ibm/java2-ppc-50
ECLIPSE_HOME=/shared/tools/mylyn/eclipse

pack() {
DIR=$ROOT/$1
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
