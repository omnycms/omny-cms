#!/bin/sh
cp settings.xml ~/.m2/settings.xml -f
cd libraries
mvn versions:set -DnewVersion=1.0.$1
mvn versions:commit
mvn deploy
cd ..
cd utilities
mvn versions:set -DnewVersion=1.0.$1
mvn versions:commit
mvn deploy
cd ..
