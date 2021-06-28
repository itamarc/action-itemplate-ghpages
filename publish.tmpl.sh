#!/bin/bash

git config user.name "[# GITHUB_ACTOR #]"
git config user.email "[# GITHUB_ACTOR #]@users.noreply.github.com"
git checkout [# INPUT_PAGES_BRANCH #]
git add .
git commit -am "action-itemplate-ghpages: Updated content"
git push --all -f https://[# GITHUB_TOKEN #]@github.com/[# GITHUB_REPOSITORY #].git

echo "To test:"
echo "GITHUB_ACTOR: $GITHUB_ACTOR"
echo "INPUT_PAGES_BRANCH: $INPUT_PAGES_BRANCH"
echo "GITHUB_TOKEN: $GITHUB_TOKEN"
echo "GITHUB_REPOSITORY: $GITHUB_REPOSITORY"
