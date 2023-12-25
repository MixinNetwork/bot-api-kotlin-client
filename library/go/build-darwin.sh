#!/bin/bash

# should install this version
#go install github.com/tougee/jvm/cmd/gomobile
#go install github.com/tougee/jvm/cmd/gobind

# should export JAVA_HOME if not set
export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home

gomobile bind -target=darwin/amd64 -x -v -o build -tags=openssl mixin/kernel