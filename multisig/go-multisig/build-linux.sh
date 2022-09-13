#!/bin/bash

# should install this version
#go install github.com/tougee/jvm/cmd/gomobile
#go install github.com/tougee/jvm/cmd/gobind

# should export JAVA_HOME if not set
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

GOMOBILE=gomobile

$GOMOBILE bind -target=linux/amd64 -x -v -o build -tags=openssl multisig-sdk