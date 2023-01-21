ARG JENKINS_IMAGE_VERSION=jenkins/jenkins:latest-jdk11
FROM $JENKINS_IMAGE_VERSION

ARG DATA_DIR=jenkinsci
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.security.csrf.GlobalCrumbIssuerConfiguration.DISABLE_CSRF_PROTECTION=true"
#ENV JENKINS_USER admin
#ENV JENKINS_PASS admin
#ENV JENKINS_ADMIN_ADRESS nobody@nowhere
ENV JENKINS_OPTS="--preferredClassLoader=java.net.URLClassLoader"

# COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/
##COPY --chown=jenkins:jenkins default-user.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY --chown=jenkins:jenkins setAdminMail.groovy /usr/share/jenkins/ref/init.groovy.d/

COPY --chown=jenkins:jenkins $DATA_DIR/jenkins_home/ /var/jenkins_home/

COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# RUN cp /usr/local/bin/jenkins.sh /usr/local/bin/jenkins_org.sh
#COPY --chown=jenkins:jenkins mylynJenkins.sh /usr/local/bin/mylynJenkins.sh
#ENTRYPOINT ["/usr/bin/tini", "--", "/usr/local/bin/mylynJenkins.sh"]