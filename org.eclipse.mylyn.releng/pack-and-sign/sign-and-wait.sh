#!/bin/sh -e

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

if [ $# -eq 0 ]
then
  echo "usage: sign-and-wait.sh directory"
  exit 1
fi

SRC=$1
DST=/home/data/httpd/download-staging.priv/tools/mylyn/hudson/signing
OUT=$DST/output

# prepare

rm -rf $DST
mkdir -p $DST
mkdir -p $OUT

# create zip

echo Creating archive for signing, output is logged to $DST/sign.log

cd $SRC
/usr/bin/find -name "org.eclipse*mylyn*.jar" | zip $DST/mylyn.zip -@ > $DST/sign.log

# sign

/usr/bin/sign $DST/mylyn.zip nomail $OUT

# wait 20 minutes for signing to complete

I=0
while [ $I -lt 40 ] && [ ! -e $OUT/mylyn.zip ]; do
  echo Waiting for $OUT/mylyn.zip
  sleep 30
  let I=I+1
done

if [ ! -e $OUT/mylyn.zip ]
then
  echo
  echo Signing Failed: Timeout waiting for $OUT/mylyn.zip
  exit 1
fi

# unzip

echo Unzipping signed files, output is logged to $DST/sign.log
/usr/bin/unzip -v -o -d $SRC $OUT/mylyn.zip >> $DST/sign.log

# cleanup

rm $DST/mylyn.zip
