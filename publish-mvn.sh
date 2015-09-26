#!/bin/sh
cp settings.xml ~/.m2/settings.xml -f
cd libraries
mvn versions:set -DnewVersion=1.$1
mvn versions:commit
mvn deploy
cd ..
