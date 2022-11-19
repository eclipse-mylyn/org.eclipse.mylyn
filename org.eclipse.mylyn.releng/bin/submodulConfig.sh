#!/bin/sh
pwd
for i in org.eclipse.mylyn*; do
  cd $i
  echo Configure autocrlf for \'$i\'
  fetchnode="`git config --local --get core.autocrlf`"
  if [ -z "$fetchnode" ]
  then
    git config --add core.autocrlf 'false'
  fi
  cd ..
done