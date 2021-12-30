#!/bin/sh
pwd
for i in org.eclipse.mylyn*; do
  cd $i
  echo Configure git for gerrit \'$i\'
  fetchnode="`git config --local --get-all remote.origin.fetch | grep "/notes"`"
  if [ -z "$fetchnode" ]
  then
    git config --add remote.origin.fetch 'refs/notes/*:refs/notes/*'
  fi
  fetchnode="`git config --local --get remote.origin.push`"
  if [ -z "$fetchnode" ]
  then
    git config --add remote.origin.push 'HEAD:refs/for/master'
  fi
  fetchnode="`git config --local --get core.autocrlf`"
  if [ -z "$fetchnode" ]
  then
    git config --add core.autocrlf 'false'
  fi
  cd ..
done