#!/bin/bash

# should install this version
#go install github.com/tougee/jvm/cmd/gomobile
#go install github.com/tougee/jvm/cmd/gobind

# build on MacOS
# brew install FiloSottile/musl-cross/musl-cross
# export CGO_ENABLED=1 GOOS=linux GOARCH=amd64 CC=x86_64-linux-musl-gcc CXX=x86_64-linux-musl-g++ 
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home

# build on Linux
# export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

gomobile bind -target=linux/amd64 -x -v -o build -tags=openssl mixin/kernel