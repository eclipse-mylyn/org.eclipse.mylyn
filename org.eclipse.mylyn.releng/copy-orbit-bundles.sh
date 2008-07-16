#!/bin/sh -e

if [ $# -eq 0 ]
then
 echo "usage: copy-orbit-bundles.sh path"
 exit 1
fi

SRC=$1/plugins
DEST=$1

pack() {
  DIR=$1
  echo Creating jar for $DIR
  (cd $DIR; zip -q ../$DIR.jar -r .)
}

cd $SRC

pack javax.xml.rpc*
pack javax.xml.soap*
pack org.apache.ant*
pack org.apache.axis*
pack org.apache.commons.discovery*

mkdir -p $DEST/standardUpdateSite/plugins
# httpclient (mylyn)
cp -v org.apache.commons.codec*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.httpclient*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.lang*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/standardUpdateSite/plugins

mkdir -p $DEST/extrasUpdateSite/plugins
# axis (jira)
cp -v javax.activation*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.mail*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.servlet*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.wsdl*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.xml.rpc*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.xml.soap*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.ant*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.axis*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.commons.discovery*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/extrasUpdateSite/plugins
# xml-rpc (trac)
cp -v javax.xml.bind*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.xmlrpc*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.ws.commons.util*.jar $DEST/extrasUpdateSite/plugins

mkdir -p $DEST/incubatorUpdateSite/plugins
# axis (xplanner)
cp -v javax.activation*.jar $DEST/incubatorUpdateSite/plugins
cp -v javax.mail*.jar $DEST/incubatorUpdateSite/plugins
cp -v javax.servlet*.jar $DEST/incubatorUpdateSite/plugins
cp -v javax.wsdl*.jar $DEST/incubatorUpdateSite/plugins
cp -v javax.xml.rpc*.jar $DEST/incubatorUpdateSite/plugins
cp -v javax.xml.soap*.jar $DEST/incubatorUpdateSite/plugins
cp -v org.apache.ant*.jar $DEST/incubatorUpdateSite/plugins
cp -v org.apache.axis*.jar $DEST/incubatorUpdateSite/plugins
cp -v org.apache.commons.discovery*.jar $DEST/incubatorUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/incubatorUpdateSite/plugins
# rome (web templates)
cp -v com.sun.syndication*.jar $DEST/incubatorUpdateSite/plugins
cp -v org.jdom*.jar $DEST/incubatorUpdateSite/plugins

mkdir -p $DEST/allUpdateSite/plugins
cp -v javax.xml.bind*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.ant*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.codec*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.httpclient*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.lang*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.xmlrpc*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.ws.commons.util*.jar $DEST/allUpdateSite/plugins
cp -v com.sun.syndication*.jar $DEST/allUpdateSite/plugins
cp -v javax.activation*.jar $DEST/allUpdateSite/plugins
cp -v javax.mail*.jar $DEST/allUpdateSite/plugins
cp -v javax.servlet*.jar $DEST/allUpdateSite/plugins
cp -v javax.wsdl*.jar $DEST/allUpdateSite/plugins
cp -v javax.xml.rpc*.jar $DEST/allUpdateSite/plugins
cp -v javax.xml.soap*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.axis*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.discovery*.jar $DEST/allUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/allUpdateSite/plugins
cp -v org.jdom*.jar $DEST/allUpdateSite/plugins

#BUILD_ROOT=$(cd $(dirname $0); pwd)
#source $BUILD_ROOT/local.sh
#cp -v $ECLIPSE_HOME_3_4/plugins/org.eclipse.ui.views.log_*jar $DEST/standardUpdateSite/plugins
#cp -v $ECLIPSE_HOME_3_4/plugins/org.eclipse.ui.views.log_*jar $DEST/allUpdateSite/plugins