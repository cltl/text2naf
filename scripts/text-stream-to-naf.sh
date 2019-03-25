#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT="$( cd $DIR && cd .. && pwd)"
LIB="$ROOT"/target


java -cp "$LIB/naf2text-v1.0-jar-with-dependencies.jar" text2naf.createNafFromText --language en --url file1


