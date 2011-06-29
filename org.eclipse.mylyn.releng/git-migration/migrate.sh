#!/bin/bash

set -e

SRC=~/tmp/cvs
DST=~/tmp/git
TEMP=~/tmp/import
CVS2GIT=~/tmp/cvs2svn-trunk/cvs2git
GITMOVEREFS=~/tmp/cvs2svn-trunk/contrib/git-move-refs.py

BASE=$(dirname $0)

MODULES="org.eclipse.mylyn.commons org.eclipse.mylyn.incubator org.eclipse.mylyn.versions org.eclipse.mylyn org.eclipse.mylyn.context org.eclipse.mylyn.tasks"

rm -rf $DST
mkdir $DST

for i in $MODULES; do 

# create cvs root with one cvs module 
rm -rf $TEMP
mkdir $TEMP
mkdir $TEMP/CVSROOT
cp -a $SRC/$i/* $TEMP/

# create git repository
mkdir $DST/$i.git
cd $DST/$i.git
git init --bare --shared

# migrate cvs
$CVS2GIT --blobfile=git-blob.dat --dumpfile=git-dump.dat --username=cvs2git $TEMP
cat git-blob.dat git-dump.dat | git fast-import

# consolidate artifical commits
$GITMOVEREFS

# pack git repository
git prune
git repack -a -d --depth=250 --window=250
git gc --aggressive
git repack -a -d --depth=250 --window=250

rm git-blob.dat git-dump.dat

# move partial tags to parent revision
$BASE/fix_tags.sh

done
