#!/bin/sh -e

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
pack org.apache.axis*
pack org.apache.commons.discovery*

mkdir -p $DEST/standardUpdateSite/plugins
cp -v javax.xml.bind*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.codec*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.httpclient*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.lang*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.xmlrpc*.jar $DEST/standardUpdateSite/plugins
cp -v org.apache.ws.commons.util*.jar $DEST/standardUpdateSite/plugins

mkdir -p $DEST/extrasUpdateSite/plugins
cp -v com.sun.syndication*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.activation*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.mail*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.servlet*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.wsdl*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.xml.rpc*.jar $DEST/extrasUpdateSite/plugins
cp -v javax.xml.soap*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.axis*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.commons.discovery*.jar $DEST/extrasUpdateSite/plugins
cp -v org.apache.commons.logging*.jar $DEST/extrasUpdateSite/plugins
cp -v org.jdom*.jar $DEST/extrasUpdateSite/plugins

mkdir -p $DEST/allUpdateSite/plugins
cp -v javax.xml.bind*.jar $DEST/allUpdateSite/plugins
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
