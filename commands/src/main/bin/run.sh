#!/bin/sh

cd `dirname $0`
CLASSPATH=../classes
for JAR in ../lib/*.jar
do
  CLASSPATH=${CLASSPATH}:$JAR
done
java -Xmx256M -cp $CLASSPATH uk.co.unclealex.music.commands.$@
