#!/bin/bash

echo content-type: application/json
echo

echo "["
c=0
for i in $(find /home/tools -name "service*.json")
do
  if [ $c != 0 ]
  then
    echo ","
  fi
  cat $i
  c=c+1
done
echo "]"