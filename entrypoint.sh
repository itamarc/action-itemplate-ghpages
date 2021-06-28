#!/bin/bash

# Run the Action main code to generate the pages
java -jar /usr/local/lib/action-itemplate-ghpages.jar

# Publish the pages int the destination branch and folder
chmod +x /usr/local/bin/publish.sh
/usr/local/bin/publish.sh
