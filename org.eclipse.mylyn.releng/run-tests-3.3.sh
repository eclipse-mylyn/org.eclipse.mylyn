#!/bin/bash

# root of build tree
BUILD_ROOT=$(cd $(dirname $0); pwd)

source $BUILD_ROOT/local.sh

echo "Installing Mylyn"
cp 3.4/build/allUpdateSite/plugins/*jar $ECLIPSE_TEST_HOME_3_3/plugins/
cp 3.3/build/standardUpdateSite/plugins/*jar $ECLIPSE_TEST_HOME_3_3/plugins/

echo "Unpacking test plug-ins"
rm -R $ECLIPSE_TEST_HOME_3_3/plugins/org.eclipse.mylyn.*tests
for i in $ECLIPSE_TEST_HOME_3_3/plugins/org.eclipse.mylyn.*tests_0.0.0.jar; do
	DIR=`echo $i | sed -e 's/_0.0.0.jar//'`
	mkdir $DIR
	unzip -o $i -d $DIR >> test.log
	rm $i
done

echo "Starting Eclipse"
$JAVA_HOME/bin/java \
 -jar $ECLIPSE_TEST_HOME_3_3/plugins/org.eclipse.equinox.launcher_*.jar \
 -clean \
 -application org.eclipse.ant.core.antRunner \
 -file $ECLIPSE_TEST_HOME_3_3/plugins/org.eclipse.mylyn.tests/test.xml \
 -Dos=linux -Dws=gtk -Darch=x86 \
 -Declipse-home=$ECLIPSE_TEST_HOME_3_3 \
 -Dlibrary-file=$ECLIPSE_TEST_HOME_3_3/plugins/org.eclipse.test_3.2.0/library.xml \
 "-Dvmargs=-Xms256M -Xmx256M" \
 "-DextraVMargs= \
    -ea \
    -Declipse.perf.dbloc=$DBLOC \
    -Declipse.perf.config=build=$QUALIFIER;config=$HOST-3.3;jvm=$JVM \
    -Declipse.perf.assertAgainst=build=$ASSERT_QUALIFIER \
    -Dmylyn.credentials=$BUILD_ROOT/credentials.properties" \
 -logger org.apache.tools.ant.DefaultLogger \
 $@
