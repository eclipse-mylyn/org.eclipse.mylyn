#!/bin/bash

set -e

#vagrant box add lucid32 http://files.vagrantup.com/lucid32.box
#vagrant init lucid32

mkdir -p target
cd target

echo "Provisioning Hudson/Jenkins"
mkdir -p hudson
cp -a ../../../../org.eclipse.mylyn.builds/org.eclipse.mylyn.hudson.releng/* hudson/
cd hudson
scripts/provision.sh
cd ..

cd ..

echo "Provisioning VM"
vagrant up
