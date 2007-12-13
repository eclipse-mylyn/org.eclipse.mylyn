#!/bin/bash

# root of build tree
BUILD_ROOT=$(cd $(dirname $0); pwd)

source $BUILD_ROOT/local.sh

unzip -o 3.4/build/allUpdateSite/allSite-$MAJOR_VERSION.$QUALIFIER.zip \
 -d $ECLIPSE_TEST_HOME_3_4

rm -R $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests
mkdir $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests
unzip -o $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests_0.0.0.jar \
 -d $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests
rm $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests_0.0.0.jar

$JAVA_HOME/bin/java \
 -jar $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.ant.core.antRunner \
 -Dos=linux -Dws=gtk -Darch=x86 \
 -Declipse-home=$ECLIPSE_TEST_HOME_3_4 \
 -Dvmargs="-Xms256M -Xmx256M" \
 -logger org.apache.tools.ant.DefaultLogger \
 -Declipse.perf.dbloc=$BUILD_ROOT/derby \
 -Declipse.perf.config="build=$QUALIFIER;host=$HOST;jvm=$JVM" \
 -Declipse.perf.assertAgainst"=build=2_0;host=$HOST;jvm=$JVM" \
 -file $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.mylyn.tests/test.xml \
 $@
