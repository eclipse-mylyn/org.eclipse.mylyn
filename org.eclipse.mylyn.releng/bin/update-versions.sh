#!/bin/bash

update() {
OLD=$1
NEW=$2
find . -name MANIFEST.MF | xargs sed -i~ -e "s/Bundle-Version: $OLD.qualifier/Bundle-Version: $NEW.qualifier/"
find . -name feature.xml | xargs sed -i~ -e "s/$OLD.qualifier/$NEW.qualifier/"
find . -name pom.xml | xargs sed -i~ -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"

find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"equivalent\".*\)/\1$NEW\2/"
find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"compatible\".*\)/\1$NEW\2/"
find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"greaterOrEqual\".*\)/\1$NEW\2/"
}

update_project() {
DIR=$1
OLD=$2
NEW=$3

cd $DIR
echo -e "Updating $DIR\t from $OLD to $NEW"
update $OLD $NEW
cd ..
}

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

PREV="3.12.0"
NEXT="3.13.0"

for dir in \
org.eclipse.mylyn \
org.eclipse.mylyn.builds \
org.eclipse.mylyn.commons \
org.eclipse.mylyn.context \
org.eclipse.mylyn.docs \
org.eclipse.mylyn.incubator \
org.eclipse.mylyn.reviews \
org.eclipse.mylyn.tasks \
org.eclipse.mylyn.versions
do
	update_project $dir $PREV $NEXT
done

cd org.eclipse.mylyn.all
echo pom.xml | xargs sed -i~ -e "s/<version>$PREV-SNAPSHOT<\/version>/<version>$NEXT-SNAPSHOT<\/version>/"
cd ..

update_project org.eclipse.mylyn.builds 1.4.0 1.5.0
update_project org.eclipse.mylyn.context 5.8.0 5.9.0 # CDT
update_project org.eclipse.mylyn.docs 2.1.0 2.2.0
update_project org.eclipse.mylyn.reviews 2.3.0 2.4.0
update_project org.eclipse.mylyn.versions 1.4.0 1.5.0

 
#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0
