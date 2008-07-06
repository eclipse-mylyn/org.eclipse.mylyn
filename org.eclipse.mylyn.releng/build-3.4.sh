#!/bin/bash

# root of build tree
BUILD_ROOT=$(cd $(dirname $0); pwd)

source $BUILD_ROOT/local.sh

POSTFIX=e3x
BUILD_HOME=$BUILD_ROOT/3.4/build

mkdir -p $BUILD_HOME/maps
cp $BUILD_ROOT/maps/mylyn_e3.4.map $BUILD_HOME/maps/
cp $BUILD_ROOT/maps/orbit.map $BUILD_HOME/maps/

$JAVA_HOME/bin/java \
 -jar $ECLIPSE_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.ant.core.antRunner \
 -buildfile $ECLIPSE_HOME_3_4/plugins/org.eclipse.pde.build_*/scripts/build.xml \
 -Dbase=$BUILD_ROOT/3.4 \
 -DbaseLocation=$ECLIPSE_HOME_3_4 \
 -Dbuilder=$BUILD_ROOT/3.4/config \
 -DforceContextQualifier=$QUALIFIER-$POSTFIX \
 -DmajorVersion=$MAJOR_VERSION \
 $*
mv $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-$POSTFIX-all.zip $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-all.zip
mv $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-$POSTFIX-e3.4.zip $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-e3.4.zip
mv $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-$POSTFIX-extras.zip $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-extras.zip
mv $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-$POSTFIX-incubator.zip $BUILD_HOME/mylyn-$MAJOR_VERSION.$QUALIFIER-incubator.zip

