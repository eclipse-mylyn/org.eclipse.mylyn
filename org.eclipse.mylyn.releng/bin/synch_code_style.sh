#!/bin/bash

reference_folder=mylyn.commons/org.eclipse.mylyn.commons.core/.settings
changed_files=""

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
	 		rc=$?
			if [ $rc -ne 0 ]
			then
				changed_files="$changed_files " $target
			fi
		fi
	fi
}

if [ ! -d $reference_folder ]
then
	echo '$0 must be run from the root org.eclipse.mylyn folder like this: org.eclipse.mylyn.releng/bin/synch_code_style.sh'
	exit 1
fi

for i in $(find . -type d -name .settings | grep -v $reference_folder | grep -v target/)
do
	process $i "org.eclipse.jdt.core.prefs"
	process $i "org.eclipse.jdt.ui.prefs"
	process $i "org.eclipse.pde.api.tools.prefs"
done
if [ "$changed_files" != "" ]
then
	git commit -qm "Updated code style" "$changed_files"
fi
