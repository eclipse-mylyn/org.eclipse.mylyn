#!/bin/bash
#

#TAG=R3_6

for TAG in $( git tag ); do

rm -f t1.txt
git log -1 --format="format:%H:%an:%b" $TAG >t1.txt
CMT=$( head -1 t1.txt | cut -f1 -d: )
if grep cvs2git t1.txt >/dev/null; then
if ! grep ^Cherrypick t1.txt >/dev/null; then
	#echo Found cvs2svn $CMT
	if [ -z "$(git log -1  --merges --format="format:%h" $TAG)" ]; then
		PT=$( git log -1 --format="format:%an" "${CMT}^1" )
		if [ "$PT" != cvs2svn ]; then
			echo git tag -f "$TAG" "${CMT}^1"
		else
			echo "cvs2svn parent: $TAG commit: $CMT"
		fi
	else
		echo "Found 2 parents: $TAG commit: $CMT"
	fi
else
	echo "Cherrypick tag: $TAG commit: $CMT"
fi
else
	echo "Tag clean: $TAG commit: $CMT"
fi

done
