#!/bin/sh
sudo pip install awscli
aws s3 cp output/omny-$1.zip s3://omny-cms-releases/omny-$1.zip
cp settings.xml ~/.m2/settings.xml -f
cd libraries
mvn versions:set -DnewVersion=1.$1
mvn versions:commit
mvn deploy
cd ..
