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

update 1.16.0 1.16.1  # builds, versions, org.eclipse.mylyn.commons.identity/notifications/repositories
#update 2.15.0 2.15.0 # reviews
update 3.24.2 3.24.3
#update 5.20.0 5.20.0 # CDT
bug=543548
version=3.24.3

# Must do this for first SR on a branch
#updateSnapshotSitesForSR 3.14

#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0

for f in `find . -maxdepth 1 -name "org.eclipse.mylyn*" -not -name "*docs"`
do
cd $f
eval "git commit -a -m \"$bug: update \$(basename \$(pwd)) versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug\""

git commit -a -m "$bug: update `basename $(pwd)` versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug"
cd ..
done