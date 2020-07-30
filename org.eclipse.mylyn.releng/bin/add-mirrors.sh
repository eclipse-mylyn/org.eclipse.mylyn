#*******************************************************************************
# Copyright (c) 2009 Tasktop Technologies and others.
# 
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
# 
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#      Tasktop Technologies - initial API and implementation
#*******************************************************************************
#!/bin/bash -e

if [ -z "$1" ]; then
 echo "usage statsId"
 exit 1
fi

if [ ! -e site.xml ]; then
 echo "missing site.xml"
 exit 1
fi

BASE=$(dirname $0)
LOCATION=$(readlink -f .)
PREFIX=/home/data/httpd/download.eclipse.org
RELATIVE=${LOCATION:${#PREFIX}}
PROJECT=${RELATIVE:1}
PROJECT=${PROJECT%%/*}

if [ "$PREFIX" != ${LOCATION:0:${#PREFIX}} ]; then
 echo "$LOCATION must be subdirectory of $PREFIX"
 exit 1
fi

if [ -z "$RELATIVE" ]; then
 echo "failed to compute path for $LOCATION"
 exit 1
fi

if [ -z "$PROJECT" ]; then
 echo "failed to compute project for $LOCATION"
 exit 1
fi


# escape slashes
MIRROR_PATH=$(echo $RELATIVE | sed s/\\//\\\\\\//g)
MIRRORS_URL="http://www.eclipse.org/downloads/download.php?file=$RELATIVE/&protocol=http&format=xml"
STATS_URI="http://download.eclipse.org/stats/$PROJECT/$1"

echo "p2.mirrorsURL: $MIRRORS_URL"
echo "p2.statsURI  : $STATS_URI"

echo "Updating site.xml"
sed -i -e 's/<site pack200=\"true\".*>/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file='$MIRROR_PATH'\/site.xml\&amp;protocol=http\&amp;format=xml">/' site.xml

if [ -e category.xml ]; then
echo "Updating mirrorsURL in category.xml to $RELATIVE"
sed -i -e 's/<site pack200=\"true\".*>/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file='$MIRROR_PATH'\/\&amp;protocol=http\&amp;format=xml">/' category.xml
fi

echo "Updating content.jar"
unzip -p content.jar | xsltproc -stringparam mirrorsURL "$MIRRORS_URL" -stringparam statsURI "$STATS_URI" $BASE/p2.xsl - > content.xml
zip content.jar content.xml
rm content.xml

echo "Updating artifacts.jar"
unzip -p artifacts.jar | xsltproc -stringparam mirrorsURL "$MIRRORS_URL" -stringparam statsURI "$STATS_URI" $BASE/p2.xsl - > artifacts.xml
zip artifacts.jar artifacts.xml
rm artifacts.xml
