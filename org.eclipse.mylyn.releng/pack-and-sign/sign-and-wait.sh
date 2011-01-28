#!/bin/bash -e

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

set -v

SRC=$1
DST=/home/data/httpd/download-staging.priv/tools/mylyn/hudson/signing
OUT=$DST/output
LOG=/home/data/httpd/download-staging.priv/arch/signer.log

# prepare

rm -rf $DST
mkdir -p $DST
mkdir -p $OUT

# create zip

echo Creating archive for signing

cd $SRC
/usr/bin/find -name "org.eclipse*mylyn*.jar" | zip $DST/mylyn.zip -@

# sign

/usr/bin/sign $DST/mylyn.zip nomail $OUT

# wait up to 30 minutes for signing to complete

tail -f $LOG | grep -E \(Extracting\|Finished\) &

I=0
while [ $I -lt 60 ] && [ ! -e $OUT/mylyn.zip ]; do
  echo Waiting for $OUT/mylyn.zip
  sleep 30
  let I=I+1
done

PID=`jobs -l -p`
kill $PID

if [ ! -e $OUT/mylyn.zip ]
then
  echo
  echo Signing Failed: Timeout waiting for $OUT/mylyn.zip
  exit 1
fi

# unzip

echo Unzipping signed files
/usr/bin/unzip -o -d $SRC $OUT/mylyn.zip

# cleanup

rm $DST/mylyn.zip
