#!/bin/sh

BASE_DIR=$(dirname $0)

CLASSPATH=$BASE_DIR/../classes
for JAR in $BASE_DIR/../lib/*.jar
do
  CLASSPATH=$CLASSPATH:$JAR
done

java -Djava.util.logging.config.file=$BASE_DIR/../conf/logging.properties -cp $CLASSPATH uk.co.unclealex.music.command.SynchroniseCommand