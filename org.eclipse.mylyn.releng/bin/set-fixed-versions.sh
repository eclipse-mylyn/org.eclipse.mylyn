find -name MANIFEST.MF | xargs sed -i -e 's/\.qualifier/.v20110316-0100/'

find -name pom.xml -not -path "*feature*" | xargs sed -i -e 's/^\(  <version>.*\)-SNAPSHOT<\/version>/\1.v20110316-0100<\/version>/'
find -name feature.xml -path "*feature*" | xargs sed -i -e 's/0\.7\.0.qualifier/0.7.1.qualifier/'
find -name pom.xml -path "*feature*" | xargs sed -i -e 's/^  <version>0.7.0-SNAPSHOT<\/version>/  <version>0.7.1-SNAPSHOT<\/version>/'
find -name feature.xml -path "*feature*" | xargs sed -i -e 's/1\.4\.0.qualifier/1.4.1.qualifier/'
find -name pom.xml -path "*feature*" | xargs sed -i -e 's/^  <version>1.4.0-SNAPSHOT<\/version>/  <version>1.4.1-SNAPSHOT<\/version>/'
find -name feature.xml -path "*feature*" | xargs sed -i -e 's/3\.5\.0.qualifier/3.5.1.qualifier/'
find -name pom.xml -path "*feature*" | xargs sed -i -e 's/^  <version>3.5.0-SNAPSHOT<\/version>/  <version>3.5.1-SNAPSHOT<\/version>/'

sed -i -e 's/^\(  <version>.*\)\.v20110316-0100<\/version>/\1-SNAPSHOT<\/version>/' pom.xml

# change root pom back to -SNAPSHOT

mvn -Dtycho.mode=maven org.sonatype.tycho:tycho-versions-plugin:set-version -DnewVersion=1.4.1-SNAPSHOT

grep Bundle-Version */META-INF/MANIFEST.MF | grep -v 0.7.0 | grep -v 3.5.0 | grep -v 1.4.0 | grep -v 0.1.0
