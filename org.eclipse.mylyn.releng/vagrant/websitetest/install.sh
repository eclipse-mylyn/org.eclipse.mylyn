#!/bin/bash

echo "Start install"
apt-get update
apt-get install -q -y apache2 mysql-server php php-mysql
apt-get clean
echo "End install"

echo "<div class=\"col-sm-24 col-md-24 col-lg-24\">Dummy page.  Update with <a href="contributorupdate.php">contributorupdate.php</a></div>" > /var/www/html/mylyn/contributors/contributor.inc
# set permissions so that contributorupdate.php can write the file
chmod  -R 777 /var/www/html/mylyn/contributors

sudo /etc/init.d/apache2 start