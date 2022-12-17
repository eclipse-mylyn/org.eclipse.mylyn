#!/bin/bash

echo "GIT_BRANCH_TAG       : $GIT_BRANCH_TAG      "
echo "BASE_URL             : $BASE_URL            "
echo "BUG_SERVICE_NAME     : $BUG_SERVICE_NAME    "
echo "EXTRAINFO            : $EXTRAINFO           "


if ! grep -q "\$answer{'urlbase'}" "/var/www/html/answers"; then
  echo "add baseurl"
  echo "\$answer{'urlbase'} = 'https://$BASE_URL/$BUG_SERVICE_NAME/';" >>/var/www/html/answers
  echo "\$answer{'sslbase'} = 'https://$BASE_URL/$BUG_SERVICE_NAME/';" >>/var/www/html/answers
fi

sed -i  "s/#bugzillaServer#/$BASE_URL/g" /etc/apache2/sites-available/bugzilla.conf
sed -i  "s/#certfileName#/$CERT_FILE_NAME/g" /etc/apache2/sites-available/bugzilla.conf

echo start mysql
service mysql start;
cd /var/www/html
./install-module.pl --all
./checksetup.pl ./answers
./checksetup.pl ./answers

# Change ownership
chown -R www-data:www-data .

# Remove stall pid file
rm -f /var/run/apache2/apache2.pid
echo 'start apache2'
# Start apache2
. /etc/apache2/envvars
/usr/sbin/apache2 -D FOREGROUND