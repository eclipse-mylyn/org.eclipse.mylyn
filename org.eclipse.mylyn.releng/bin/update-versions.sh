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

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

update 1.1.1 1.1.2
update 1.8.1 1.8.2
update 2.0.1 2.0.2
update 3.9.1 3.9.2
update 5.5.1 5.5.2

 
#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0
