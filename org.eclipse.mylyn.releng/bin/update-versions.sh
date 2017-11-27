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

#update 1.15.0 1.15.1 # builds, versions, org.eclipse.mylyn.commons.identiy/notifications/repositories
#update 2.14.1 2.14.2 # reviews
#update 3.23.1 3.23.2
#update 5.19.1 5.19.2 # CDT
bug=527820
version=3.23.2

# Must do this for first SR on a branch
#updateSnapshotSitesForSR 3.23

#grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.9.0 | grep -v 3.7.0 | grep -v 1.5.0

for x in `find  -maxdepth 1 -not -name docs | ls -d */`; do
cd $x
eval "git commit -a -m \"$bug: update \$(basename \$(pwd)) versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug\""
cd ..
done

git commit -a -m "$bug: update `basename $(pwd)` versions to $version

Change-Id: I0000000000000000000000000000000000000000
Task-Url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=$bug"