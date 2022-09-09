#!/bin/bash

export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

GOMOBILE=gomobile

$GOMOBILE bind -target=linux/amd64 -x -v -o build -tags=openssl multisig-sdk