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

if [ ! -e site.xml ]; then
 echo "missing site.xml"
 exit 1
fi

LOCATION=$(readlink -f .)
PREFIX=/home/data/httpd/download.eclipse.org
RELATIVE=${LOCATION:${#PREFIX}}

if [ "$LOCATION" != ${LOCATION:0:${#PREFIX}} ]; then
 echo "$LOCATION must be subdirectory of $PREFIX"
 exit 1
fi

if [ -z "$RELATIVE" ]; then
 echo "failed to compute path for $LOCATION"
 exit 1
fi

# escape slashes
RELATIVE=$(echo $RELATIVE | sed s/\\//\\\\\\//g)

echo "Updated mirrorsURL in site.xml to $RELATIVE"
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=$PATH\/site.xml\&amp;protocol=http\&amp;format=xml">/' site.xml

echo "Updated mirrorsURL in category.xml to $RELATIVE"
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=$PATH\/\&amp;protocol=http\&amp;format=xml">/' category.xml
