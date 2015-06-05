#!/bin/bash -e

pushd /home/data/httpd/download.eclipse.org/mylyn/drops > /dev/null

echo "<html><head><meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />" > index.html.temp
echo "<meta http-equiv="Pragma" content="no-cache" />" >> index.html.temp
echo "<meta http-equiv="Expires" content="0" /></head>" >> index.html.temp

echo "<body><h1>Mylyn Snapshot Builds</h1>" >> index.html.temp
echo "<p>Weekly builds are production quality, but subject to UI changes.</p><p>" >> index.html.temp
for f in `find -name *3.1*.zip -not -name *-api.zip | sort`; do 
	echo "<a href=\"$f\">${f##.*/}</a><br/>" >> index.html.temp
done
echo "</p></body><html>" >> index.html.temp

cp index.html.temp index.html
popd > /dev/null
