#!/bin/bash -e

if [ $# -eq 0 ]
then
 echo "usage: upload.sh username [-extract]"
 echo "                 local [-force]"
 exit 1
fi


BUILD_ROOT=$(cd $(dirname $0); pwd)
source $BUILD_ROOT/local.sh

SITE_ARCHIVE=$BUILD_ROOT/site.zip
SITE=$BUILD_ROOT/tmp/site

pack() {
  rm -r $SITE || true
  rm tmp/site.tar || true
  rm $SITE_ARCHIVE || true

  mkdir -p $SITE

  cp -a $BUILD_ROOT/3.3/build/standardUpdateSite $SITE/e3.3
  cp -a $BUILD_ROOT/3.3/build/mylyn-$MAJOR_VERSION.$QUALIFIER-e3.3.zip $SITE

  cp -a $BUILD_ROOT/3.4/build/mylyn-$MAJOR_VERSION.$QUALIFIER-e3.4.zip $SITE
  cp -a $BUILD_ROOT/3.4/build/mylyn-$MAJOR_VERSION.$QUALIFIER-extras.zip $SITE
  cp -a $BUILD_ROOT/3.4/build/mylyn-$MAJOR_VERSION.$QUALIFIER-incubator.zip $SITE
  cp -a $BUILD_ROOT/3.4/build/mylyn-wikitext-standalone-$MAJOR_VERSION.$QUALIFIER_incubation.zip $SITE
  cp -a $BUILD_ROOT/3.4/build/standardUpdateSite $SITE/e3.4
  cp -a $BUILD_ROOT/3.4/build/extrasUpdateSite $SITE/extras
  cp -a $BUILD_ROOT/3.4/build/incubatorUpdateSite $SITE/incubator

  (cd tmp/site; find -name "*.jar" -or -name "site.xml" | zip -r $SITE_ARCHIVE -@)

  tar -C $SITE -cvf tmp/site.tar .
}

if [ "$1" == "local" ]
then
  pack
  cp tmp/site.tar $HOME
  $HOME/downloads/tools/mylyn/extract-site.sh $MAJOR_VERSION $QUALIFIER $2 -weekly
else
  if [ "$2" != "-extract" ]
  then
    pack
    scp tmp/site.tar $1@dev.eclipse.org:
  else
    PARM=-force
  fi
  ssh $1@dev.eclipse.org downloads/tools/mylyn/extract-site.sh $MAJOR_VERSION $QUALIFIER $PARM -weekly
fi
