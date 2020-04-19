#!/bin/bash

mvn install:install-file \
    -Dfile=../coresource-1.0.jar \
    -DgroupId=ru.spbstu.gyboml \
    -DartifactId=core \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true
