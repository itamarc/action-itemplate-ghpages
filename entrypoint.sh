#!/bin/bash

# check values
if [ -z "${GITHUB_TOKEN}" ]; then
    echo "Error: not found GITHUB_TOKEN"
    exit 1
fi

if [ -z "${INPUT_PAGES_BRANCH}" ]; then
    export INPUT_PAGES_BRANCH=master
fi

# Run the Action main code to generate the pages
java -jar /usr/local/lib/action-itemplate-ghpages.jar

# Publish the pages in the destination branch and folder
git config user.name "${GITHUB_ACTOR}"
git config user.email "${GITHUB_ACTOR}@users.noreply.github.com"
export GIT_CURRBRANCH=`git branch --show-current`
if [ "${GIT_CURRBRANCH}" != "${INPUT_PAGES_BRANCH}" ]; then
    git stash save
    git checkout ${INPUT_PAGES_BRANCH}
    git stash pop
fi
git add .
git commit -am "action-itemplate-ghpages: Updated content"
git push --all -f "https://${GITHUB_ACTOR}:${GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git"
