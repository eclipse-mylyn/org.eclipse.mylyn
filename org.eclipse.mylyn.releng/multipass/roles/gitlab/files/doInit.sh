#! /bin/bash -e

BASEDIR=$(dirname "$0")
echo "$BASEDIR"

until [ "`docker inspect -f {{.State.Health.Status}} gitlab`" == "healthy" ]; do
    sleep 10;
done;
echo "gitlab is healthy"
docker exec gitlab gitlab-rails runner /etc/gitlab/initialSetup.rb >>$BASEDIR/initialSetup.out 2>&1
echo "gitlab $BASEDIR/initialSetup.out"
cat $BASEDIR/initialSetup.out

export GITLAB_SERVER=https://gitlab.mylyn.local
export GITLAB_TOKEN=glpat-Adm1nPwdTok123

export GITLAB_ROOTGROUP=eclipse-mylyn
export GITLAB_SUBGROUP=ci-test
export GITLAB_PROJECT=ci-mylyn-test-project

#
#creating root group
#
echo "gitlab creating root group"
rootGroupId=$(curl -k --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "$GITLAB_SERVER/api/v4/groups?search=$GITLAB_ROOTGROUP" | jq '.[0]["id"]' )
if [ "$rootGroupId" == "null" ] 
then
  rootGroupId=$(curl -k -d "name=$GITLAB_ROOTGROUP&path=$GITLAB_ROOTGROUP&visibility=private&lfs_enabled=true&description=Root group" -X POST "$GITLAB_SERVER/api/v4/groups" -H "PRIVATE-TOKEN: $GITLAB_TOKEN" | jq '.["id"]')
  echo "Root group created with Id: $rootGroupId"
fi

#
#creating sub group
#
echo "gitlab creating sub group"
rootSubGroupId=$(curl -k --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "$GITLAB_SERVER/api/v4/groups/$rootGroupId/subgroups?search=$GITLAB_SUBGROUP" | jq '.[0]["id"]' )
if [ "$rootSubGroupId" == "null" ] 
then
  rootSubGroupId=$(curl -k -d "name=$GITLAB_SUBGROUP&path=$GITLAB_SUBGROUP&visibility=private&lfs_enabled=true&description=$GITLAB_SUBGROUP programme&parent_id=$rootGroupId" -X POST "$GITLAB_SERVER/api/v4/groups" -H "PRIVATE-TOKEN: $GITLAB_TOKEN" | jq '.["id"]')
  echo "Sub group created with Id: $rootSubGroupId"
fi

#
#project creation
#
projectId=$(curl -k "$GITLAB_SERVER/api/v4/groups/$rootSubGroupId/projects?search=$GITLAB_PROJECT" -H "PRIVATE-TOKEN: $GITLAB_TOKEN" | jq '.[0]["id"]' )
if [ "$projectId" == "null" ] 
then
  projectId=$(curl -k -d "path=$GITLAB_PROJECT&namespace_id=$rootSubGroupId" -X POST "$GITLAB_SERVER/api/v4/projects" -H "PRIVATE-TOKEN: $GITLAB_TOKEN" | jq '.["id"]')
  echo "Project created with Id: $projectId"
fi

echo "Root group Id: $rootGroupId"
echo "Sub group Id: $rootSubGroupId"
echo "Project Id: $projectId"

# if test -f "/home/ubuntu/.ssh/known_hosts"; then
#   ssh-keygen -f "/home/ubuntu/.ssh/known_hosts" -R "[mylyn.local]:2222"
# fi
curl -k -X POST -F "private_token=$GITLAB_TOKEN" -F "title=Ubuntu SSH" -F "key=$(cat ~/.ssh/id_rsa.pub)" https://gitlab.mylyn.local/api/v4/user/keys

#
#Clonning git project and generating basic java maven structure
# 
cd $BASEDIR
mvn archetype:generate -B -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DgroupId=mylyn.test.group -DartifactId=$GITLAB_PROJECT -Dversion=0.0.1
cd $GITLAB_PROJECT
git init
git config --global http.sslverify false
git config --global user.email "admin@mylyn.eclipse.org"
git config --global user.name "Mylyn Admin"

git remote add origin ssh://git@gitlab.mylyn.local:2222/$GITLAB_ROOTGROUP/$GITLAB_SUBGROUP/$GITLAB_PROJECT.git
git add .
git commit -m "Initial commit"
GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no" git push -u origin master 
#
# Git branches initial structure as per gitflow
#

#create develop branch
curl -k -d "branch=develop&ref=master" -X POST "$GITLAB_SERVER/api/v4/projects/$projectId/repository/branches" -H "PRIVATE-TOKEN: $GITLAB_TOKEN" | jq

ls -al $BASEDIR/initialSetup.out
cat $BASEDIR/initialSetup.out
