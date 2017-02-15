#!/bin/bash
clear
echo "Installing gradle"
sudo apt-get install gradle
echo "Build gradle"
gradle build
echo "The jar file is now in p1/build/libs/compiler.jar"
