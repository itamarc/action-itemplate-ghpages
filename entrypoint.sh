#!/bin/bash

# check values
if [ -z "${GITHUB_TOKEN}" ]; then
    echo "Error: not found GITHUB_TOKEN"
    exit 1
fi

if [ -z "${BRANCH_NAME}" ]; then
   export BRANCH_NAME=master
fi

# Run the Action main code to generate the pages
java -jar /usr/local/lib/action-itemplate-ghpages.jar

# git is needed for the publish process
apt-get -y install git

# Publish the pages int the destination branch and folder
chmod +x /usr/local/bin/publish.sh
/usr/local/bin/publish.sh
