FROM 127.0.0.1:5000/bugzillabase:1

MAINTAINER Frank Becker <eclipse@frank-becker.de>
ARG GIT_BRANCH_TAG=5.2
ARG EXTRAINFO=none

ENV GIT_BRANCH_TAG=$GIT_BRANCH_TAG
ENV EXTRAINFO=$EXTRAINFO
ENV BASE_URL http://localhost
ENV BUG_SERVICE_NAME empty

ADD src /opt/

# Run build script
RUN echo GIT_BRANCH_TAG=$GIT_BRANCH_TAG EXTRAINFO=$EXTRAINFO
RUN mv /opt/answersV$GIT_BRANCH_TAG /opt/answers
RUN /opt/build.sh GIT_BRANCH_TAG=$GIT_BRANCH_TAG EXTRAINFO="$EXTRAINFO"

# Run start script
CMD ["/opt/start.sh"]

# Expose web server port
EXPOSE 80