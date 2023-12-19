#!/bin/bash

# should install this version
#go install github.com/tougee/jvm/cmd/gomobile
#go install github.com/tougee/jvm/cmd/gobind

# build on MacOS
# brew install mingw-w64
# export CGO_ENABLED=1 GOOS=windows GOARCH=amd64 CC=x86_64-w64-mingw32-gcc CXX=x86_64-w64-mingw32-g++
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

gomobile bind -target=windows/amd64 -x -v -o build -tags=openssl mixin/kernel