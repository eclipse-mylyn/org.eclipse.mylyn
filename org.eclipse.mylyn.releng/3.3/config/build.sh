#!/bin/sh

#------ BEGIN USER SETTINGS
# Mylyn's major version
MAJOR_VERSION=2.2.0

# Qualifier format: IYYYYMMDD-HHMM
QUALIFIER=I20071220-1230

# root of build tree
BUILD_HOME=/home/releng/org.eclipse.mylyn/org.eclipse.mylyn.releng/3.3

export JAVA_HOME=/home/releng/jdk1.6.0_03

#------ END USER SETTINGS
rm -R ../build
mkdir ../build
mkdir ../build/maps
cp ../../maps/mylyn_e3.3.map ../build/maps/

cd $BUILD_HOME/eclipse/plugins/org.eclipse.pde.build_3.3.2.R331_v20071019/scripts
$JAVA_HOME/bin/java -jar $BUILD_HOME/eclipse/plugins/org.eclipse.equinox.launcher_1.0.1.R33x_v20070828.jar  -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=$BUILD_HOME -DbaseLocation=$BUILD_HOME/eclipse -Dbuilder=$BUILD_HOME/config -DforceContextQualifier=$QUALIFIER  -DmajorVersion=$MAJOR_VERSION

