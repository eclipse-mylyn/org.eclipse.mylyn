#!/bin/sh -e

BASE=/home/tools/trac

if [ $# -ne 2 ]
then
 echo usage install.sh trac-version genshi-version
 exit 1
fi

mkdir -p $BASE/share/trac-$1/bin
mkdir -p $BASE/share/trac-$1/lib
export PYTHONPATH=$BASE/share/trac-$1/lib

cd genshi-$2
python setup.py install --prefix=$BASE/share/trac-$1 --install-lib=$BASE/share/trac-$1/lib
