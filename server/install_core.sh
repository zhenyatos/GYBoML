#!/bin/bash

mvn install:install-file \
    -Dfile=../client/coresource/build/libs/coresource-1.0.jar \
    -DgroupId=ru.spbstu.gyboml \
    -DartifactId=core \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true
