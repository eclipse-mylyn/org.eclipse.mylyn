#!/bin/bash -e

update() {
source composite.index

#if [ "$DIRS" == "" ]; then
#  echo "missing DIRS in $PWD/composite.index"
#  exit 1
#fi

if [ "$NAME" == "" ]; then
  echo "missing NAME in $PWD/composite.index"
  exit 1
fi

FILE=compositeArtifacts.xml
TAG=compositeArtifactRepository
TYPE=org.eclipse.equinox.internal.p2.artifact.repository.CompositeArtifactRepository

compose

FILE=compositeContent.xml
TAG=compositeMetadataRepository
TYPE=org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository

compose
}

compose() {
echo "Updating $PWD/$FILE"

cat > $FILE <<EOF
<?xml version='1.0' encoding='UTF-8'?>
<?TAG version='1.0.0'?>
<repository name='NAME' type='TYPE' version='1.0.0'>
  <properties size='2'>
    <property name='p2.compressed' value='true'/>
    <property name='p2.timestamp' value='TIMESTAMP'/>
  </properties>
  <children size='CHILD_COUNT'>
EOF

sed -i -e "s/TAG/$TAG/" -e "s/TYPE/$TYPE/" $FILE
sed -i -e "s/NAME/$NAME/" -e "s/TIMESTAMP/$TIMESTAMP/" $FILE
COUNT=0
for i in $DIRS; do
  echo "    <child location='$i'/>" >> $FILE
  COUNT=$((COUNT+1))
 
  echo " added $i"
done
sed -i -e "s/CHILD_COUNT/$COUNT/" $FILE

cat >> $FILE <<EOF
  </children>
</repository>
EOF
}


TIMESTAMP=$(date +%s)000

if [ "$1" == "-r" ]; then
 for i in $(find -name composite.index); do
  (cd $(dirname $i); update)
  setfacl -m u:55011:rwx $(dirname $i)/*.xml || true
 done
elif [ -e composite.index ]; then
 update
else
 echo "composite.index not found"
 exit 1
fi

$(dirname $0)/generate-snapshot-index.sh