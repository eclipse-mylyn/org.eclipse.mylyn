#!/bin/bash

set -e
set -v

SRC=~/tmp/cvs
CVSROOT=/cvsroot/mylyn
BASE=$(dirname $0)

rm -rf $SRC
cp -a $CVSROOT $SRC

cd $SRC

# remove obsolete files excluded from migration
find -name .refactorings -type d | xargs rm -rf 
find -name "*.jar,v" -path "*Attic*" | xargs rm 
find org.eclipse.mylyn.commons/org.eclipse.mylyn/ -name Attic -not -path "*src/*" -not -path org.eclipse.mylyn.commons/org.eclipse.mylyn/Attic -not -path org.eclipse.mylyn.commons/org.eclipse.mylyn/.settings/Attic | xargs rm -rf
rm -rf org.eclipse.mylyn/org.eclipse.mylyn.releng/scripts/tools/
rm -f org.eclipse.mylyn/org.eclipse.mylyn.tests.performance/Attic/resourceExclusionTestPaths.txt,v
rm -rf org.eclipse.mylyn/Attic

# reorganize modules
mv org.eclipse.mylyn/org.eclipse.mylyn.tests.performance* org.eclipse.mylyn/org.eclipse.mylyn.tests.ui org.eclipse.mylyn.incubator/
mv org.eclipse.mylyn/org.eclipse.mylyn.help.* org.eclipse.mylyn/org.eclipse.mylyn.sdk-feature/ org.eclipse.mylyn/org.eclipse.mylyn.test-feature/ org.eclipse.mylyn.tasks/
mv org.eclipse.mylyn.contexts org.eclipse.mylyn.context

# disable hooks
sed -i -e s/^LockDir/#LockDir/ CVSROOT/config
sed -i -e s/^ALL/#ALL/ CVSROOT/taginfo

# remove obsolete tags
for i in $(cat $BASE/tags.txt); do 

echo Removing $i
cvs -d $PWD rtag -d $i .

done
