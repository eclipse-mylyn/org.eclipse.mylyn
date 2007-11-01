#!/bin/sh

mkdir ../build
cp -R ../../maps ../build/

BUILD_HOME=/path/to/base/dir/3.4

export JAVA_HOME=/path/to/jdk/jdk1.6.0_03

rm -R $BUILD_HOME/build/standardUpdateSite/*
rm -R $BUILD_HOME/build/extrasUpdateSite/*
rm -R $BUILD_HOME/build/experimentalUpdateSite/*
cd $BUILD_HOME/eclipse3.3/plugins/org.eclipse.pde.build_3.3.2.R331_v20071019/scripts

$JAVA_HOME/bin/java -jar $BUILD_HOME/eclipse3.3/plugins/org.eclipse.equinox.launcher_1.0.1.R33x_v20070828.jar  -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=$BUILD_HOME -DbaseLocation=$BUILD_HOME/eclipse3.4 -Dbuilder=$BUILD_HOME/config

