#!/bin/sh
rm -R ../build
mkdir ../build
cp -R ../../maps ../build/

BUILD_HOME=/home/build/mylyn/org.eclipse.mylyn/org.eclipse.mylyn.releng/3.4

export JAVA_HOME=/home/build/mylyn/jdk/jdk1.6.0_03

cd $BUILD_HOME/eclipse3.3/plugins/org.eclipse.pde.build_3.3.2.R331_v20071019/scripts

$JAVA_HOME/bin/java -jar $BUILD_HOME/eclipse3.3/plugins/org.eclipse.equinox.launcher_1.0.1.R33x_v20070828.jar  -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbase=$BUILD_HOME -DbaseLocation=$BUILD_HOME/eclipse3.4 -Dbuilder=$BUILD_HOME/config
