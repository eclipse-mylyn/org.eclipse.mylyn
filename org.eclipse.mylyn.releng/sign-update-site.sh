#!/bin/bash -e

if [ $# -lt 2 ]
then
  echo "usage: sign-update-site.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

SRC=/home/data/httpd/download.eclipse.org/tools/mylyn/update-archive/$MAJOR/$BUILD
DST=/opt/public/download-staging.priv/tools/mylyn
OUT=$DST/output
TMP=$DST/tmp/$MAJOR-$BUILD
JAVA_HOME=/opt/ibm/java2-ppc-50
ECLIPSE_HOME=/shared/tools/mylyn/eclipse

unzip() {
 /bin/rm -R $TMP/$1 || true
 /bin/mkdir -p $TMP/$1
 /usr/bin/unzip -d $TMP/$1 $SRC/mylyn-$MAJOR.$BUILD-$1.zip
}

rezip() {
 cd $TMP/$1
 /usr/bin/zip $TMP/mylyn-$MAJOR.$BUILD-$1.zip -r .
}

pack() {
DIR=$TMP/$1
$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.update.core.siteOptimizer \
 -jarProcessor -verbose -processAll -repack -pack \
 -digestBuilder -digestOutputDir=$DIR -siteXML=$DIR/site.xml \
 -outputDir $DIR $DIR

$JAVA_HOME/bin/java \
 -Xmx512m \
 -jar $ECLIPSE_HOME/plugins/org.eclipse.equinox.launcher_*.jar \
 -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator \
 -updateSite $DIR \
 -site file:$DIR/site.xml \
 -metadataRepository file:$DIR \
 -metadataRepositoryName "$2 "\
 -artifactRepository file:$DIR \
 -artifactRepositoryName "$2" \
 -compress \
 -reusePack200Files \
 -noDefaultIUs
}

if [ ! -e $TMP ]
then

 # extract site

 /bin/rm $TMP || true
 unzip e3.3
 unzip e3.4
 unzip extras
 unzip incubator

 /bin/rm $DST/mylyn.zip || true
 cd $TMP
 /usr/bin/find -name "org.eclipse.mylyn*.jar" | zip $DST/mylyn.zip -@

 # sign

 mkdir -p $OUT
 /bin/rm $OUT/mylyn.zip || true
 /usr/bin/sign $DST/mylyn.zip nomail $OUT
fi

I=0
while [ $I -lt 20 ] && [ ! -e $OUT/mylyn.zip ]; do
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

# repack site

/usr/bin/unzip -o -d $TMP $OUT/mylyn.zip
rezip e3.3
rezip e3.4
rezip extras
rezip incubator

pack e3.3 "Mylyn for Eclipse 3.3"
pack e3.4 "Mylyn for Eclipse 3.4"
pack extras "Mylyn Extras"
pack incubator "Mylyn Incubator"

# republish

/bin/mv $SRC $SRC-DELETE
/bin/cp -av $TMP $SRC
/bin/chgrp -R mylynadmin $SRC
/bin/chmod g+w -R $SRC
/bin/chmod o+r -R $SRC
/usr/bin/find $SRC -type d | xargs chmod +x
rm -R $SRC-DELETE
