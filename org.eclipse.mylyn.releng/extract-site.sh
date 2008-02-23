#!/bin/sh -e

if [ $# -lt 2 ]
then
  echo "usage: extract-site.sh version qualifier [-force] [-weekly]"
  exit 1
fi

TARGET=$HOME/downloads/tools/mylyn/update-archive/$1/$2

if [ -e $TARGET ] && [ "$3" != "-force" ]
then
  echo "Target folder already exists: $TARGET"
  exit
fi

mkdir -p $TARGET 
tar -C $TARGET -xvf $HOME/site.tar

cd $TARGET
chgrp -R mylynadmin .
chmod g+w -R .

echo
echo
echo Created site in $TARGET
ls $TARGET/*

if [ "$3" == "-weekly" ] || [ "$4" == "-weekly" ]
then
  WEEKLY_TARGET=$HOME/downloads/tools/mylyn/update/weekly

  rm -r $WEEKLY_TARGET.old | true
  mv $WEEKLY_TARGET $WEEKLY_TARGET.old

  cp -a $TARGET $WEEKLY_TARGET

  echo
  echo Updated weekly site in $WEEKLY_TARGET
  ls $WEEKLY_TARGET/*
fi
