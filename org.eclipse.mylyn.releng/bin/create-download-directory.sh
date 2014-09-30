#!/bin/bash

createDownloadDirectory() {
	OLD=$1
	NEW=$2
	DIR=$3
	
	mkdir $DIR/drops/$NEW/
	setfacl -m user:hudsonBuild:rwx $DIR/drops/$NEW/
	# change ownership to current user so that setfacl will be allowed
	cp -r $DIR/drops/$OLD $DIR/drops/${OLD}_copy
	rm -rf $DIR/drops/$OLD
	mv $DIR/drops/${OLD}_copy/ $DIR/drops/$OLD
	setfacl -R -x user:hudsonBuild $DIR/drops/$OLD/
}

OLD=$1
NEW=$2
MAJOR=$3

createDownloadDirectory $OLD $NEW ~/downloads/mylyn

if [ -n "$MAJOR" ]; then
	# Create site
	cp -a ~/downloads/mylyn/snapshots/${OLD%.0} ~/downloads/mylyn/snapshots/${NEW%.0}
	
	# Create Incubator download directory
	createDownloadDirectory $OLD $NEW ~/downloads/mylyn/incubator
	cp -a ~/downloads/mylyn/incubator/${OLD%.0} ~/downloads/mylyn/incubator/${NEW%.0}
fi



