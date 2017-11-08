#!/bin/bash

export $(eval "printf 'EPUB_CORE_VERSION=\${project.version}' | mvn -f core/pom.xml help:evaluate | grep '^EPUB_CORE_VERSION'")

export $(eval "printf 'EPUB_UI_VERSION=\${project.version}' | mvn help:evaluate | grep '^EPUB_UI_VERSION'")

echo "core version: $EPUB_CORE_VERSION"
echo "ui version:   $EPUB_UI_VERSION"

if [ "$EPUB_CORE_VERSION" == "$EPUB_UI_VERSION" ];
then
  echo "Versions are the same, nothing to do!"
  exit 0
fi

update() {
  OLD=$1
  NEW=$2

  OLD_BARE=$(eval "echo $OLD | sed -E 's/([^-]+)-[A-Z]+/\1/'")
  NEW_BARE=$(eval "echo $NEW | sed -E 's/([^-]+)-[A-Z]+/\1/'")

  OLD_QUALIFIED="$OLD_BARE.qualifier"
  NEW_QUALIFIED="$NEW_BARE.qualifier"

  echo "Updating $OLD to $NEW..."
  echo "Updating $OLD_QUALIFIED to $NEW_QUALIFIED..."

  find . -name pom.xml | xargs sed -i~ -e "s/<version>$OLD<\/version>/<version>$NEW<\/version>/"
  find . -name ui-pom.xml | xargs sed -i~ -e "s/<version>$OLD<\/version>/<version>$NEW<\/version>/"
  find .. -name docs-pom.xml | xargs sed -i~ -e "s/<version>$OLD<\/version>/<version>$NEW<\/version>/"
  find .. -name pom.xml | xargs sed -i~ -e "s/<version>$OLD<\/version>/<version>$NEW<\/version>/"

  find . -name MANIFEST.MF | xargs sed -i~ -e "s/Bundle-Version: $OLD_QUALIFIED/Bundle-Version: $NEW_QUALIFIED/"
  find . -name MANIFEST.MF | xargs sed -i~ -e "s/\(org\.eclipse\.mylyn\.docs\.epub\..*;bundle-version=\"\)$OLD_BARE\(\"\)/\1$NEW_BARE\2/"
  find . -name feature.xml | xargs sed -i~ -e "s/$OLD_QUALIFIED/$NEW_QUALIFIED/"
}

update $EPUB_UI_VERSION $EPUB_CORE_VERSION

echo "done."

git status
