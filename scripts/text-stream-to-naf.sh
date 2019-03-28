#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target

# example how to call: echo "This is a text" | ./text-stream-to-naf.sh

java -cp "$LIB/text2naf-1.0-SNAPSHOT-jar-with-dependencies.jar" createNafFromText --language en --uri "http://www.newsreader-project.eu/example-news.html"


