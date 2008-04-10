#!/bin/bash -e

if [ $# -eq 0 ]
then
 echo "usage: upload.sh username"
 exit 1
fi


BUILD_ROOT=$(cd $(dirname $0); pwd)
source $BUILD_ROOT/local.sh

SITE=$BUILD_ROOT/tmp/site

rm -r $SITE || true
rm tmp/site.tar || true

mkdir -p $SITE

#cp -a $BUILD_ROOT/3.3/build/standardUpdateSite $SITE/e3.3
cp -a $BUILD_ROOT/3.4/build/standardUpdateSite $SITE/e3.4
cp -a $BUILD_ROOT/3.4/build/extrasUpdateSite $SITE/extras
cp -a $BUILD_ROOT/3.4/build/experimentalUpdateSite $SITE/experimental

tar -C $SITE -cvf tmp/site.tar .
scp tmp/site.tar $1@download1.eclipse.org:
ssh $1@download1.eclipse.org downloads/tools/mylyn/extract-site.sh $MAJOR_VERSION $QUALIFIER -dev
