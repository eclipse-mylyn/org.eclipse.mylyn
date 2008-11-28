#!/bin/sh -e

ROOT=$HOME/downloads/tools/mylyn/update

echo "Updating site mirrors"
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=\/tools\/mylyn\/update\/e3.3\/site.xml\&amp;protocol=http\&amp;format=xml">/' $ROOT/e3.3/site.xml
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=\/tools\/mylyn\/update\/e3.4\/site.xml\&amp;protocol=http\&amp;format=xml">/' $ROOT/e3.4/site.xml
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=\/tools\/mylyn\/update\/extras\/site.xml\&amp;protocol=http\&amp;format=xml">/' $ROOT/extras/site.xml
sed -i -e 's/<site pack200=\"true\">/<site pack200=\"true\" mirrorsURL="http:\/\/www.eclipse.org\/downloads\/download.php?file=\/tools\/mylyn\/update\/incubator\/site.xml\&amp;protocol=http\&amp;format=xml">/' $ROOT/incubator/site.xml

echo
echo "Done"