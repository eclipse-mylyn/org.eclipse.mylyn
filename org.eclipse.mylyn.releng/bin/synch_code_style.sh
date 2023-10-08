#!/bin/bash

reference_folder=mylyn.commons/org.eclipse.mylyn.commons.core/.settings
added_file=0

process() {
	target="$1/$2"
	if [ -f $target ]
	then
		source="$reference_folder/$2"
 		diff -q $source $target >/dev/null
 		rc=$?
		if [ $rc -ne 0 ]
		then
			echo "Changed: $target"
			cp $source $target
			git add $target
			added_file=1
		fi		
	fi
}

if [ ! -d $reference_folder ]
then
	echo '$0 must be run from the root Mylyn folder'
	exit 1
fi

for i in $(find . -type d -name .settings | grep -v $reference_folder)
do
	process $i "org.eclipse.jdt.core.prefs"
	process $i "org.eclipse.jdt.ui.prefs"+
done
if [ $added_file -ne 0 ]
then
	git commit -qm "Synched code styles"
fi
	
