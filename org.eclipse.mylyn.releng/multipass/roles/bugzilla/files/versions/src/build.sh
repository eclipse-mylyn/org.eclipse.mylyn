#!/bin/bash
for ARGUMENT in "$@"
do
	KEY=$(echo $ARGUMENT | cut -f1 -d=)
	VALUE=$(echo $ARGUMENT | cut -f2 -d=)

	case "$KEY" in
			GIT_BRANCH_TAG)	GIT_BRANCH_TAG=${VALUE} ;;
			EXTRAINFO)		EXTRAINFO=${VALUE} ;;
			*)
	esac
done

CUSTOM_WF=false
CUSTOM_WF_AND_STATUS=false
USEBUGALIASES=false
APIKEYENABLED=false
USEREMAIL=false

echo "extrainfo : $EXTRAINFO"
EXTRAINFO1=${EXTRAINFO// /}

IFS=',' read -ra my_array <<< "$EXTRAINFO1"
for i in "${my_array[@]}"
do
	IFS=':' read -ra entry_array <<< "$i"
	name=${entry_array[0]}
	value=${entry_array[1]}
	if [ "$name" = "\"custom_wf\"" ]; then
		if [ "$value" = "\"true\"" ]; then
		  CUSTOM_WF=true
		fi
	fi
	if [ "$name" = "\"custom_wf_and_status\"" ]; then
		if [ "$value" = "\"true\"" ]; then
		  CUSTOM_WF_AND_STATUS=true
		fi
	fi
	if [ "$name" = "\"use_bug_alias\"" ]; then
		if [ "$value" = "\"true\"" ]; then
		  USEBUGALIASES=true
		fi
	fi
	if [ "$name" = "\"api_key_enabled\"" ]; then
		if [ "$value" = "\"true\"" ]; then
		  APIKEYENABLED=true
		fi
	fi
done

#echo "CUSTOM_WF             : >$CUSTOM_WF<"
#echo "CUSTOM_WF_AND_STATUS  : >$CUSTOM_WF_AND_STATUS<"
#echo "USEBUGALIASES         : >$USEBUGALIASES<"
#echo "APIKEYENABLED         : >$APIKEYENABLED<"
#echo "GIT_BRANCH_TAG        : >$GIT_BRANCH_TAG<"
#echo "EXTRAINFO             : >$EXTRAINFO<"

rm -rf /var/www/html
git clone --branch $GIT_BRANCH_TAG https://github.com/bugzilla/bugzilla.git /var/www/html/

service mysql start;
mysql -u root -p' ' < /opt/mysql_init.sql;
cp /opt/answers /var/www/html/
#cp /opt/test.html /var/www/html/
cp /opt/bugzilla.conf /etc/apache2/sites-available/

if  [[ $GIT_BRANCH_TAG == release-4.4* ]] ; then
cp /var/www/html/Bugzilla/CGI.pm /var/www/html/Bugzilla/CGI_org.pm
cp /opt/CGI_4.4.pm /var/www/html/Bugzilla/CGI.pm
fi

if  [[ $GIT_BRANCH_TAG == release-5.0* ]] ; then
cp /var/www/html/Bugzilla/CGI.pm /var/www/html/Bugzilla/CGI_org.pm
cp /opt/CGI_5.0.pm /var/www/html/Bugzilla/CGI.pm
fi

if  [[ $GIT_BRANCH_TAG == release-5.1* ]] ; then
cp /var/www/html/Bugzilla/CGI.pm /var/www/html/Bugzilla/CGI_org.pm
cp /opt/CGI_5.1.pm /var/www/html/Bugzilla/CGI.pm
USEREMAIL=true
fi
if  [[ $GIT_BRANCH_TAG == 5.2* ]] ; then
cp /var/www/html/Bugzilla/CGI.pm /var/www/html/Bugzilla/CGI_org.pm
cp /opt/CGI_5.2.pm /var/www/html/Bugzilla/CGI.pm
fi

cp -r /opt/extensions/Mylyn /var/www/html/extensions/Mylyn
if [ "$USEBUGALIASES" = "true" ]; then
	echo "\$answer{'usebugaliases'} = '1';" >>/var/www/html/answers
fi
if [ "$CUSTOM_WF_AND_STATUS" = "true" ]; then
	echo "\$answer{'duplicate_or_move_bug_status'} = 'VERIFIED';" >>/var/www/html/answers
fi
erb userMail=$USEREMAIL api_key_enabled=$APIKEYENABLED custom_wf=$CUSTOM_WF custom_wf_and_status=$CUSTOM_WF_AND_STATUS /opt/Extension.pm.erb >/var/www/html/extensions/Mylyn/Extension.pm


a2enmod rewrite cgi headers expires alias ssl #proxy
a2ensite bugzilla
a2dissite 000-default