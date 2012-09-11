#!/bin/sh -e

if [ $# -ne 3 ]
then
 echo usage install.sh src prefix version
 exit 1
fi

SRC=$1
PREFIX=$2
VERSION=$3

/bin/mkdir -p $PREFIX/bin
/bin/mkdir -p $PREFIX/lib
export PYTHONPATH=$PREFIX/lib

cd $SRC
/usr/bin/python setup.py install --prefix=$PREFIX --install-lib=$PREFIX/lib --install-data=$PREFIX/data
/bin/cp -a trac/htdocs $PREFIX

/bin/touch $PREFIX/lib/.provisioned
