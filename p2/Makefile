SHELL := /bin/bash

all: install build copy

install:
	-[ -z `command -v gradle` ] && echo "Installing Gradle" && sudo apt-get install gradle

clean:
	gradle clean
	rm -f ./compiler.jar
	rm -f ./testing/compiler.jar

test:
	gradle test

build: FORCE
	gradle build

copy:
	cp ./build/libs/compiler.jar ./
	cp ./compiler.jar ./testing/

FORCE:

run-tokens: build copy
	java -ea -jar compiler.jar $(src) --tokens

run-grammar: build copy
	java -ea -jar compiler.jar $(src) --grammar

run-ast: build copy
	java -ea -jar compiler.jar $(src) --ast

grade: clean all
	(cd testing && ./grade.sh)
