#!/bin/sh -e

ROOT=$(cd $(dirname $0); pwd)/update

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
mv $ROOT/mylyn-$OLD_VERSION_SHORT* $BACKUP

ls $BACKUP
echo

# promote

echo "Promoting weekly build"

WEEKLY=$ROOT/weekly
cp -a $WEEKLY/e3.3 $ROOT
cp -a $WEEKLY/e3.4 $ROOT
cp -a $WEEKLY/extras $ROOT

NEW_VERSION=`grep mylyn_feature.*version $ROOT/e3.3/site.xml | sed 's/.*\"\([^\"]*\)\"./\1/' | head`
NEW_VERSION_SHORT=`echo $NEW_VERSION | sed 's/\(.*\)\..*/\1/'`

if [ "$NEW_VERSION" == "" ] || [ "$NEW_VERSION_SHORT" == "" ]
then
    echo "Could not determine new version"
    exit 1
fi

echo "Updating site archives"

cp $ROOT/e3.3/mylyn-$NEW_VERSION*.zip $ROOT/mylyn-$NEW_VERSION_SHORT-e3.3.zip 
cp $ROOT/e3.4/mylyn-$NEW_VERSION*.zip $ROOT/mylyn-$NEW_VERSION_SHORT-e3.4.zip 
cp $ROOT/extras/mylyn-$NEW_VERSION*.zip $ROOT/mylyn-$NEW_VERSION_SHORT-extras.zip 

echo "Updating mirror ulrs"
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/e3.3/site.xml
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/e3.4/site.xml
sed -i -e 's/http:\/\/download.eclipse.org\/tools\/mylyn\/update\/weekly\//http:\/\/download.eclipse.org\/tools\/mylyn\/update\//' $ROOT/extras/site.xml

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
echo Archives
ls $ROOT/mylyn*.zip


