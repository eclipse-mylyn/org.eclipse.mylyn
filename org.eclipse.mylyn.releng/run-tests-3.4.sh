#!/bin/bash

# root of build tree
BUILD_ROOT=$(cd $(dirname $0); pwd)

source $BUILD_ROOT/local.sh

rm -rf $ECLIPSE_TEST_HOME_3_4/dropins/mylyn
mkdir $ECLIPSE_TEST_HOME_3_4/dropins/mylyn

echo "Installing Mylyn"
cp -a 3.4/build/allUpdateSite/features 3.4/build/allUpdateSite/plugins $ECLIPSE_TEST_HOME_3_4/dropins/mylyn

echo "Unpacking features"
for i in $ECLIPSE_TEST_HOME_3_4/dropins/mylyn/features/*.jar; do
	DIR=`echo $i | sed -e 's/.jar//'`
	mkdir $DIR
	unzip -o $i -d $DIR >> test.log
	rm $i
done

echo "Unpacking test plug-ins"
# unpack test plug-ins for resource access
for i in $ECLIPSE_TEST_HOME_3_4/dropins/mylyn/plugins/org.eclipse.mylyn.*tests_0.0.0.jar; do
	DIR=`echo $i | sed -e 's/_0.0.0.jar//'`
	mkdir $DIR
	unzip -o $i -d $DIR >> test.log
	rm $i
done

echo "Starting Eclipse to run tests"
$JAVA_HOME/bin/java \
 -jar $ECLIPSE_TEST_HOME_3_4/plugins/org.eclipse.equinox.launcher_*.jar \
 -clean \
 -application org.eclipse.ant.core.antRunner \
 -file $ECLIPSE_TEST_HOME_3_4/dropins/mylyn/plugins/org.eclipse.mylyn.tests/test.xml \
 -Declipse-home=$ECLIPSE_TEST_HOME_3_4 \
 -Dlibrary-file=$ECLIPSE_TEST_HOME_3_4/dropins/eclipse/plugins/org.eclipse.test_3.2.0/library.xml \
 "-DextraVMargs= \
    -ea \
    -Declipse.perf.dbloc=$BUILD_ROOT/derby \
    -Declipse.perf.config=build=$QUALIFIER;config=$HOST-3.4;jvm=$JVM \
    -Declipse.perf.assertAgainst=build=$ASSERT_QUALIFIER \
    -Dmylyn.credentials=$BUILD_ROOT/credentials.properties" \
 -Dos=linux -Dws=gtk -Darch=x86 \
 "-Dvmargs=-Xms256M -Xmx256M" \
 -logger org.apache.tools.ant.DefaultLogger \
 $@
