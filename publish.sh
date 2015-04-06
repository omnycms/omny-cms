#!/bin/sh
sudo pip install awscli
aws s3 cp output/omny-$1.zip s3://omny-releases/omny-$1.zip
