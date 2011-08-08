update() {
OLD=$1
NEW=$2
find -name MANIFEST.MF | xargs sed -i -e "s/Bundle-Version: $OLD.qualifier/Bundle-Version: $NEW.qualifier/"
find -name feature.xml | xargs sed -i -e "s/$OLD.qualifier/$NEW.qualifier/"
find -name pom.xml | xargs sed -i -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"

find -name feature.xml | xargs sed -i -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"equivalent\".*\)/\1$NEW\2/"
}

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

update 0.8.0 0.9.0
update 1.5.0 1.6.0
update 3.6.0 3.7.0
update 5.2.200 5.3.0

 
#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0
