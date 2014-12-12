#!/bin/bash
# Prints suggestions for updating versions of plugins and features in a target from an update site
# Usage: ./extractVersionsFromUpdateSite.sh targetFile updateSite [startFrom]
# e.g. : ./extractVersionsFromUpdateSite.sh ../../org.eclipse.mylyn-target/mylyn-e4.4.target ~/downloads/releases/luna/201409261001/
# If the optional startFrom paramter is specified, the script starts from the first line containing startFrom. 

checkDirectory() {
	local updateSite=$1
	local unit=$2
	local unit2=$unit
	unit2+="_"
	for line in `ls $updateSite`; do
		if [[ $line == $unit2* ]] && [[ $line != *".gz" ]]
		then
			local ver=`echo $line | sed 's$.*_\(.*\)\.jar$\1$'`
			if [[ $updateSite == */features ]]
			then
				echo \<unit id=\"$unit.feature.group\" version=\"$ver\"/\>
			else
				echo \<unit id=\"$unit\" version=\"$ver\"/\>
			fi
		fi
	done
}

printVersions() {
	local targetFile=$1
	local updateSite=$2
	local startFrom=$3
	local reachedStart=0
	if  [[ $startFrom == "" ]] 
	then
		reachedStart=1
	fi
	while read line; do
		if [[ $reachedStart == 0 ]] && [[ $line == *$startFrom* ]]
		then
			reachedStart=1
		elif [ "$reachedStart" == 1 ]
		then
			local unit=`echo $line | sed 's$<unit id="\(.*\).feature.group" version=.*$\1$'`
			if [ "$unit" != "" ] && [ "$unit" != "$line" ]
			then
				checkDirectory ${updateSite}features $unit
			else
				local unit=`echo $line | sed 's$<unit id="\(.*\)" version=.*$\1$'`
				if [ "$unit" != "" ] && [ "$unit" != "$line" ]
				then
				checkDirectory ${updateSite}plugins $unit
				fi
			fi
		fi
	done < $targetFile
}

pushd `pwd -P` > /dev/null
printVersions $1 $2 $3
popd > /dev/null
