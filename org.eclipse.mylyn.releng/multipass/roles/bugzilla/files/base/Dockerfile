FROM ubuntu:18.04
MAINTAINER Frank Becker <Eclipse@Frank-Becker.de>

ADD apt_package.list /opt/bugzilla_base/apt_package.list

# Install apache, PHP, and supplimentary programs.
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -q -y `cat /opt/bugzilla_base/apt_package.list` \
 && apt-get clean

#RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*