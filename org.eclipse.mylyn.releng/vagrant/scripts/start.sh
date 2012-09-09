#!/bin/bash

set -e

if [ ! -e /vagrant ]
then
  echo "This script is intented to be run from within a vagrant VM"
  exit 1
fi

cd /vagrant/target

cat > proxy.conf <<EOF
<Proxy *>
  Order deny,allow
</Proxy>

ProxyRequests       Off
ProxyPreserveHost   Off
EOF
sudo cp proxy.conf /etc/apache2/conf.d/proxy.conf


cd hudson
sudo cp hudson.conf /etc/apache2/conf.d
scripts/start.sh
cd ..

sudo service apache2 reload
