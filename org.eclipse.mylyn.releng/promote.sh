#!/bin/sh -e

if [ $# -lt 2 ]
then
  echo "usage: promote.sh major build"
  exit 1
fi

MAJOR=$1
BUILD=$2

ROOT=~/downloads/tools/mylyn/update

# backup old release 

OLD_VERSION=`grep mylyn_feature.*version $ROOT/e3.3/site.xml | sed 's/.*\"\([^\"]*\)\"./\1/' | head`
OLD_VERSION_SHORT=`echo $OLD_VERSION | sed 's/\(.*\)\..*/\1/'`

if [ "$OLD_VERSION" == "" ] || [ "$OLD_VERSION_SHORT" == "" ]
then
    echo "Could not determine old version"
    exit 1
fi

BACKUP=$ROOT/$OLD_VERSION-DELETE
echo "Archiving old version to $BACKUP"

mkdir -p $BACKUP
mv $ROOT/e3.3 $BACKUP
mv $ROOT/e3.4 $BACKUP
mv $ROOT/extras $BACKUP
mv $ROOT/incubator $BACKUP
mv $ROOT/mylyn-$OLD_VERSION_SHORT* $BACKUP

ls $BACKUP
echo

# promote

echo "Promoting $MAJOR.$BUILD"

WEEKLY=$ROOT-archive/$MAJOR/$BUILD
cp -a $WEEKLY/e3.3 $ROOT
cp -a $WEEKLY/e3.4 $ROOT
cp -a $WEEKLY/extras $ROOT
cp -a $WEEKLY/incubator $ROOT

NEW_VERSION=`grep mylyn_feature.*version $ROOT/e3.3/site.xml | sed 's/.*\"\([^\"]*\)\"./\1/' | head`
NEW_VERSION_SHORT=`echo $NEW_VERSION | sed 's/\(.*\)\..*/\1/'`

if [ "$NEW_VERSION" == "" ] || [ "$NEW_VERSION_SHORT" == "" ]
then
    echo "Could not determine new version"
    exit 1
fi

echo "Updating site archives"

cp $WEEKLY/mylyn-$NEW_VERSION*-e3.3.zip $ROOT/mylyn-$NEW_VERSION_SHORT-e3.3.zip 
cp $WEEKLY/mylyn-$NEW_VERSION*-e3.4.zip $ROOT/mylyn-$NEW_VERSION_SHORT-e3.4.zip 
cp $WEEKLY/mylyn-$NEW_VERSION*-extras.zip $ROOT/mylyn-$NEW_VERSION_SHORT-extras.zip
cp $WEEKLY/mylyn-$NEW_VERSION*-incubator.zip $ROOT/mylyn-$NEW_VERSION_SHORT-extras.zip  

echo "Updating mirror ulrs"
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/e3.3/site.xml
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/e3.4/site.xml
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/extras/site.xml
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/incubator/site.xml

echo "Done"

echo
echo $ROOT/e3.3
ls $ROOT/e3.3
echo
echo $ROOT/e3.4
ls $ROOT/e3.4
echo
echo $ROOT/extras
ls $ROOT/extras
echo
echo $ROOT/incubator
ls $ROOT/incubator
echo
echo Archives
ls $ROOT/mylyn*.zip
