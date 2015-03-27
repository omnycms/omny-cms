#!/bin/sh
sudo pip install awscli
aws s3 cp output/omny-$1.zip s3://omny-releases/omny-$1.zip
sudo wget -qO- https://get.docker.com/ | sh
sudo docker build -t "alamarre/omny-cms:$1" .
sudo docker login -e $DOCKER_EMAIL -p $DOCKER_PASSWORD
sudo docker push alamarre/omny-cms:$1
