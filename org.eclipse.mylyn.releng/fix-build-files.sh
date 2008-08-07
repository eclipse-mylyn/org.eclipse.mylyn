#!/bin/sh -e

if [ $# -eq 0 ]
then
 echo "usage: fix-build-files.sh path"
 exit 1
fi

sed 's/<antcall target=\"refresh\"\/>//' -i~ $1/features/org.eclipse.mylyn*/build.xml
