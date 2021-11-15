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

if [ $# -lt 3 ]
then
  echo "usage: sign-and-wait.sh srcdir signdir filter"
  exit 1
fi

set -x

SRC=$1

cd $SRC

echo Begin Signing

for file in `/usr/bin/find -name "*$3*.jar"`; do
	echo Signing $file
	curl -o $file -F file=@$file https://cbi.eclipse.org/jarsigner/sign
done

echo Completed Signing