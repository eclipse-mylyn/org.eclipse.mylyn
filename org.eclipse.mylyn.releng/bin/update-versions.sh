update() {
OLD=$1
NEW=$2
echo "Updating $OLD to $NEW..."
find . -name MANIFEST.MF | xargs sed -i~ -e "s/Bundle-Version: $OLD.qualifier/Bundle-Version: $NEW.qualifier/"
find . -name MANIFEST.MF | xargs sed -i~ -e "s/\(org\.eclipse\.mylyn\..*;bundle-version=\"\)$OLD\(\"\)/\1$NEW\2/"
find . -name feature.xml | xargs sed -i~ -e "s/$OLD.qualifier/$NEW.qualifier/"
find . -name pom.xml | xargs sed -i~ -e "s/<version>$OLD-SNAPSHOT<\/version>/<version>$NEW-SNAPSHOT<\/version>/"

find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"equivalent\".*\)/\1$NEW\2/"
find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"compatible\".*\)/\1$NEW\2/"
find . -name feature.xml | xargs sed -i~ -e "s/\(mylyn.* version=\"\)$OLD\(\" match=\"greaterOrEqual\".*\)/\1$NEW\2/"
}

updateSnapshotSitesForSR() {
MAJOR_MINOR=$1
echo "Updating snapshot sites to $MAJOR_MINOR"
sed -i~ -e "s#http://download.eclipse.org/mylyn/snapshots/nightly.*/<#http://download.eclipse.org/mylyn/snapshots/$MAJOR_MINOR/<#" \
org.eclipse.mylyn/org.eclipse.mylyn-parent/pom.xml
}

#mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

update 1.15.0 1.16.0 # builds, versions, org.eclipse.mylyn.commons.identiy/notifications/repositories
update 2.14.0 2.15.0 # reviews
update 3.23.0 3.24.0
update 5.19.0 5.20.0 # CDT
bug=515140
version=3.24

# Must do this for first SR on a branch
#updateSnapshotSitesForSR 3.14

#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0

git submodule foreach eval "git commit -a -m \"$bug: update \$(basename \$(pwd)) versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug\""

git commit -a -m "$bug: update `basename $(pwd)` versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug"