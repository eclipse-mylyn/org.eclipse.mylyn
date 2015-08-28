#!/bin/bash
# print the p2.mirrorsURL property from content.jar

set -e
cd $1
mkdir metadata-test
cd metadata-test
unzip ../content.jar
grep "mirrors" content.xml
cd ..
rm -rf metadata-test