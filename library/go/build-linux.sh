#!/bin/bash

# should install this version
#go install github.com/tougee/jvm/cmd/gomobile
#go install github.com/tougee/jvm/cmd/gobind

# build on MacOS
# brew install FiloSottile/musl-cross/musl-cross
# export CGO_ENABLED=1 GOOS=linux GOARCH=amd64 CC=x86_64-linux-musl-gcc CXX=x86_64-linux-musl-g++ 
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

GOMOBILE=gomobile
$GOMOBILE bind -target=linux/amd64 -x -v -o build -tags=openssl mixin/kernel