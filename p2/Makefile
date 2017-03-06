all: clean build copy

install:
	sudo apt-get install gradle

clean:
	./gradlew clean
	rm -f ./compiler.jar

test:
	./gradlew test

build:
	./gradlew build

copy:
	cp ./build/libs/compiler.jar ./