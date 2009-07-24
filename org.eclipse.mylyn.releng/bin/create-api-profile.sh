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

if [ $# -lt 2 ]
then
  echo "usage: create-api-profile.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/$BUILD
OUT=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/mylyn-$MAJOR-api.zip
TMP=/shared/tools/mylyn/tmp/profile

rm -rf $TMP || true
mkdir -p $TMP

rm $OUT || true
/usr/bin/find $SRC -path "*plugins*" -not -path "*e3.3*" -name "org.eclipse.mylyn*.jar" -not -name "*source*" | xargs -i cp {} $TMP
zip $OUT -j $TMP/*
