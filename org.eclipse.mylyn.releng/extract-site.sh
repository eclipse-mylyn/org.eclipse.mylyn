#!/bin/sh -e

release() {
  WEEKLY_TARGET=$HOME/downloads/tools/mylyn/update/$1

  rm -r $WEEKLY_TARGET.old | true
  mv $WEEKLY_TARGET $WEEKLY_TARGET.old

  cp -a $TARGET $WEEKLY_TARGET

  echo
  echo Updated site at $WEEKLY_TARGET
  ls $WEEKLY_TARGET/*
}

if [ $# -lt 2 ]
then
  echo "usage: extract-site.sh version qualifier [-force] [-weekly|-dev]"
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
	release weekly
fi

if [ "$3" == "-dev" ] || [ "$4" == "-dev" ]
then
	release dev
fi
