#!/bin/sh
# Maven Wrapper script for Unix/Linux/macOS/Git Bash
# Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.

set -e

MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Read distributionUrl from properties
DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2)

# Default Maven home inside user home
MAVEN_USER_HOME="${HOME}/.m2/wrapper/dists"
MAVEN_ZIP_NAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_HOME="$MAVEN_USER_HOME/$MAVEN_ZIP_NAME/apache-maven-3.9.6"

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    mkdir -p "$MAVEN_USER_HOME/$MAVEN_ZIP_NAME"
    TMP_ZIP="$MAVEN_USER_HOME/$MAVEN_ZIP_NAME/${MAVEN_ZIP_NAME}.zip"
    echo "Downloading Maven from $DISTRIBUTION_URL..."
    curl -o "$TMP_ZIP" "$DISTRIBUTION_URL" 2>/dev/null || wget -O "$TMP_ZIP" "$DISTRIBUTION_URL"
    unzip -q "$TMP_ZIP" -d "$MAVEN_USER_HOME/$MAVEN_ZIP_NAME"
    rm -f "$TMP_ZIP"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
