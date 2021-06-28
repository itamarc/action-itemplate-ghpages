#!/bin/bash

git config user.name "[# GITHUB_ACTOR #]"
git config user.email "[# GITHUB_ACTOR #]@users.noreply.github.com"
git checkout [# INPUT_PAGES_BRANCH #]
git add .
git commit -am "action-itemplate-ghpages: Updated content"
git push --all -f https://[# ACTION_PUBLISH_TOKEN #]@github.com/[# GITHUB_REPOSITORY #].git
