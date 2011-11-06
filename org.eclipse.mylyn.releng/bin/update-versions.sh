#!/bin/bash

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

update_qualifier() {
OLD=$1
QUALIFIER=$2
find -not -path "*feature*" -name MANIFEST.MF | xargs sed -i -e "s/Bundle-Version: $OLD.qualifier/Bundle-Version: $OLD.$QUALIFIER/"
find -mindepth 3 -not -path "*feature*" -not -path "*org.eclipse.mylyn.reviews*" -name pom.xml | xargs sed -i -e "s/^  <version>$OLD-SNAPSHOT<\/version>/  <version>$OLD.$QUALIFIER<\/version>/"
find -mindepth 4 -not -path "*feature*" -path "*org.eclipse.mylyn.reviews*" -name pom.xml | xargs sed -i -e "s/^  <version>$OLD-SNAPSHOT<\/version>/  <version>$OLD.$QUALIFIER<\/version>/"
}

update_features() {
OLD=$1
NEW=$2

# feature versions
find -path "*feature*" -name feature.xml | xargs sed -i -e "s/$OLD.qualifier/$NEW.qualifier/"
find -path "*feature*" -name pom.xml | xargs sed -i -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"

# feature dependencies
find -path "*feature*" -name feature.xml | xargs sed -i -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"equivalent\".*\)/\1$NEW\2/"

# parent poms
find -name pom.xml | xargs sed -i -e "s/^    <version>$OLD-SNAPSHOT<\/version>/    <version>$NEW-SNAPSHOT<\/version>/"
find -maxdepth 2 -not -path "*feature*" -not -path "*org.eclipse.mylyn.reviews*" -name pom.xml | xargs sed -i -e "s/^  <version>$OLD-SNAPSHOT<\/version>/  <version>$NEW-SNAPSHOT<\/version>/"
find -maxdepth 3 -not -path "*feature*" -path "*org.eclipse.mylyn.reviews*" -name pom.xml | xargs sed -i -e "s/^  <version>$OLD-SNAPSHOT<\/version>/  <version>$NEW-SNAPSHOT<\/version>/"

# org.eclipse.mylyn
find org.eclipse.mylyn/org.eclipse.mylyn.releng -name pom.xml | xargs sed -i -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"
sed -i -e "s/<mylyn-version>$OLD-SNAPSHOT<\/mylyn-version>/<mylyn-version>$NEW-SNAPSHOT<\/mylyn-version>/" org.eclipse.mylyn/org.eclipse.mylyn.releng/pom.xml

}

update_features 0.8.3 0.8.4
update_features 1.5.3 1.5.4
update_features 3.6.3 3.6.4
update_features 5.2.303 5.2.304

#update_qualifier 0.8.0 v20110608-1400
#update_qualifier 1.5.0 v20110608-1400
#update_qualifier 3.6.0 v20110608-1400

#echo "Fix version in org.eclipse.mylyn/org.eclipse.mylyn.releng"

#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0
