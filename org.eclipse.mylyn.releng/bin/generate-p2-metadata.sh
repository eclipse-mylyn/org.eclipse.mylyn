#*******************************************************************************
# Copyright (c) 2009 Tasktop Technologies and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#      Tasktop Technologies - initial API and implementation
#*******************************************************************************
#!/bin/bash -e

if [ $# -lt 1 ]; then
 ROOT=$PWD
else
 ROOT=$1
fi
if [ $# -lt 2 ]; then
 NAME="Mylyn for Eclipse 3.5, 3.6 and 3.7"
else
 NAME="$2"
fi

if [ -z "$JAVA_HOME" ]; then
 echo "JAVA_HOME is not set"
 exit 1
fi
if [ -z "$ECLIPSE_HOME" ]; then
 echo "ECLIPSE_HOME is not set"
 exit 1
fi

pack() {
DIR=$1
SITEXML=$DIR/category.xml
if [ ! -e $SITEXML ]; then
 SITEXML=$DIR/site.xml
fi
if [ ! -e $SITEXML ]; then
 echo "$SITEXML not found"
 exit 1
fi

echo Processing $DIR using $SITEXML

rm -f $DIR/artifacts.jar $DIR/content.jar $DIR/digest.zip

#$JAVA_HOME/bin/java \
# -Xmx512m \
# -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
# -application org.eclipse.update.core.siteOptimizer \
# -verbose -processAll \
# -digestBuilder -digestOutputDir=$DIR -siteXML=$DIR/site.xml || true
 
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
 -source $DIR \
 -metadataRepository file:$DIR \
 -metadataRepositoryName "$2 "\
 -artifactRepository file:$DIR \
 -artifactRepositoryName "$2" \
 -compress \
 -reusePack200Files \
 -publishArtifacts

$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
 -metadataRepository file:$DIR \
 -categoryDefinition file:$SITEXML \
 -compress \
 -categoryQualifier

chmod 664 $DIR/artifacts.jar $DIR/content.jar #$DIR/digest.zip
}

pack "$ROOT" "$NAME"
