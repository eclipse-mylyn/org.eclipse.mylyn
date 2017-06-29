#!/bin/bash -e

pushd /home/data/httpd/download.eclipse.org/mylyn/drops > /dev/null

echo "<html><head><meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />" > index.html
echo "<meta http-equiv="Pragma" content="no-cache" />" >> index.html
echo "<meta http-equiv="Expires" content="0" /></head>" >> index.html

echo "<body><h1>Mylyn Snapshot Builds</h1>" >> index.html
echo "<p>Weekly builds are production quality, but subject to UI changes.</p><p>" >> index.html
for f in `find -name *3.[1-4]*.zip -not -name *-api.zip | sort`; do 
	echo "<a href=\"https://www.eclipse.org/downloads/download.php?file=/mylyn/drops${f#\.}\">${f##.*/}</a><br/>" >> index.html
done
echo "</p></body><html>" >> index.html

popd > /dev/null
