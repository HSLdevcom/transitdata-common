#!/bin/bash

POM_VERSION=$(xpath pom.xml //project/version/text\(\))
echo "Version from pom.xml $POM_VERSION"

newRev="travis-bintray-deploy.json"
rm -f $newRev

sed "s/POM_VERSION/$POM_VERSION/g" travis-bintray-deploy.template.json > $newRev

echo "Bintray deploy json:"
cat $newRev
