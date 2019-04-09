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

if [ $# -lt 2 ]
then
  echo "usage: create-api-profile.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/mylyn/drops/$MAJOR/$BUILD
OUT=/home/data/httpd/download.eclipse.org/mylyn/drops/$MAJOR/mylyn-$MAJOR-api.zip
TMP=$HOME/tmp/profile

rm -rf $TMP || true
mkdir -p $TMP

rm $OUT || true
/usr/bin/find $SRC -path "*plugins*" -not -path "*e3.3*" -name "org.eclipse.mylyn*.jar" -not -name "*source*"  -not -name "*.tests_*" | xargs -i cp {} $TMP
zip $OUT -j $TMP/*
