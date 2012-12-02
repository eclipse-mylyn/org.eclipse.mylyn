update() {
OLD=$1
NEW=$2
find -name MANIFEST.MF | xargs sed -i -e "s/Bundle-Version: $OLD.qualifier/Bundle-Version: $NEW.qualifier/"
find -name feature.xml | xargs sed -i -e "s/$OLD.qualifier/$NEW.qualifier/"
find -name pom.xml | xargs sed -i -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"

find -name feature.xml | xargs sed -i -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"equivalent\".*\)/\1$NEW\2/"
}

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

update 1.0.2 1.0.3
update 1.7.2 1.7.3
update 3.8.2 3.8.3
update 5.4.2 5.4.3

 
#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0
