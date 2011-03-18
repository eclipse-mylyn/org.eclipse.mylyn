#*******************************************************************************
# Copyright (c) 2009 Tasktop Technologies and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
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

LOCATION=$(readlink -f .)
PREFIX=/home/data/httpd/download.eclipse.org
RELATIVE=${LOCATION:${#PREFIX}}

if [ "$PREFIX" != ${LOCATION:0:${#PREFIX}} ]; then
 echo "$LOCATION must be subdirectory of $PREFIX"
 exit 1
fi

if [ -z "$RELATIVE" ]; then
 echo "failed to compute path for $LOCATION"
 exit 1
fi

# escape slashes
MIRROR_PATH=$(echo $RELATIVE | sed s/\\//\\\\\\//g)

echo "Updating mirrorsURL in site.xml to $RELATIVE"
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file='$MIRROR_PATH'\/site.xml\&amp;protocol=http\&amp;format=xml">/' site.xml

if [ -e category.xml ]; then
echo "Updating mirrorsURL in category.xml to $RELATIVE"
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file='$MIRROR_PATH'\/\&amp;protocol=http\&amp;format=xml">/' category.xml
fi

MIRRORS_URL="http://www.eclipse.org/downloads/download.php?file=$MIRROR_PATH/&amp;protocol=http&amp;format=xml"
STATS_URI="http://mylyn.eclipse.org/stats/$1

echo "Updating p2.mirrorsURL in content.jar to $MIRRORS_URL and p2.statsURI to $STATS_URI"
unzip -p content.jar | xsltproc -stringparam mirrorsURL http://foo/bar -stringparam statsURI http://stats/ /home/spingel/mylyn/src/org.eclipse.mylyn/org.eclipse.mylyn.releng/scripts/p2.xsl - > content.xml
zip content.jar content.xml
rm content.xml

echo "Updating p2.mirrorsURL in artifacts.jar to $MIRRORS_URL and p2.statsURI to $STATS_URI"
unzip -p artifacts.jar | xsltproc -stringparam mirrorsURL http://foo/bar -stringparam statsURI http://stats/ /home/spingel/mylyn/src/org.eclipse.mylyn/org.eclipse.mylyn.releng/scripts/p2.xsl - > artifacts.xml
zip artifacts.jar artifacts.xml
rm artifacts.xml
