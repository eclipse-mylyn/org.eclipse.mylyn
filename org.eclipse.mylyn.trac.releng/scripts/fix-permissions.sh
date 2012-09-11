#!/bin/sh
USER=`grep www /etc/passwd | sed s/:.*//`
find var/ -name trac.ini | xargs chown $USER
find var/ -name db -type d | xargs chown -R $USER
find var/ -name log -type d | xargs chown -R $USER
find var/ -name attachments -type d | xargs chown -R $USER
chown -R $USER var/svn
chmod 775 bin/*
