#!/bin/bash

set -e

if [ ! -e /vagrant ]
then
  echo "This script is intented to be run from within a vagrant VM"
  exit 1
fi

cd /vagrant/target

cd hudson
scripts/stop.sh
cd ..
