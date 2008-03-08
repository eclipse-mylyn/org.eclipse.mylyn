#!/bin/bash -e

if [ $# -eq 0 ]
then
 echo "usage: get-orbit-entries.sh url"
 exit 1
fi

BUILD_ROOT=$(cd $(dirname $0); pwd)
MAP=$BUILD_ROOT/maps/orbit.map

wget $1 -O $MAP

echo vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
echo 

grep "^plugin@com.sun.syndication,0.9.0" $MAP
grep "^plugin@javax.activation,1.1.0" $MAP
grep "^plugin@javax.mail,1.4.0" $MAP
grep "^plugin@javax.xml.bind,2.0.0" $MAP
grep "^plugin@javax.xml.rpc,1.1.0" $MAP
grep "^plugin@javax.xml.soap,1.2.0" $MAP
grep "^plugin@javax.wsdl,1.5.1" $MAP
grep "^plugin@org.apache.axis,1.4.0" $MAP
grep "^plugin@org.apache.commons.codec,1.3.0" $MAP
grep "^plugin@org.apache.commons.discovery,0.2.0" $MAP
grep "^plugin@org.apache.commons.httpclient,3.1.0" $MAP
grep "^plugin@org.apache.commons.lang,2.3.0" $MAP
grep "^plugin@org.apache.commons.logging,1.0.4" $MAP
grep "^plugin@org.apache.xmlrpc,3.0.0" $MAP
grep "^plugin@org.apache.ws.commons.util,1.0.0" $MAP
grep "^plugin@org.jdom,1.0.0" $MAP

echo
echo ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
echo All entries found.