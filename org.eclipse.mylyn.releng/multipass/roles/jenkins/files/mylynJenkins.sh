#! /bin/bash -e

echo "start jenkins-plugin-cli "
#ls -al /var/jenkins_home
#ls -al /bin/jenkins-plugin-cli
#ls -al /usr/share/jenkins/ref/plugins.txt
/bin/jenkins-plugin-cli --verbose \
   --jenkins-experimental-update-center=https://cdn.jsdelivr.net/gh/lework/jenkins-update-center/updates/huawei/update-center.json \
   --jenkins-incrementals-repo-mirror=https://cdn.jsdelivr.net/gh/lework/jenkins-update-center/updates/huawei/update-center.json \
   -f /usr/share/jenkins/ref/plugins.txt
echo "ende jenkins-plugin-cli "

echo "JENKINS_USER:         $JENKINS_USER"
echo "JENKINS_PASS:         $JENKINS_PASS"
echo "JENKINS_ADMIN_ADRESS: $JENKINS_ADMIN_ADRESS"
echo "JENKINS_URL:          $JENKINS_URL"

#sed -i "s|#JENKINS_URL#|$JENKINS_URL|g" /usr/share/jenkins/ref/init.groovy.d/setAdminMail.groovy
#sed -i "s|#JENKINS_ADMIN_ADRESS#|$JENKINS_ADMIN_ADRESS|g" /usr/share/jenkins/ref/init.groovy.d/setAdminMail.groovy

echo "finally run original entrypoint shell script"
. /usr/local/bin/jenkins.sh
