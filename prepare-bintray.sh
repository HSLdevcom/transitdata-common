#!/bin/bash

POM_VERSION=$(xpath -q -e '/project/version/text()' pom.xml)
echo "Version from pom.xml $POM_VERSION"

newRev="travis-bintray-deploy.json"
rm -f $newRev

sed "s/POM_VERSION/$POM_VERSION/g" travis-bintray-deploy.template.json > $newRev

echo "Bintray deploy json:"
cat $newRev
