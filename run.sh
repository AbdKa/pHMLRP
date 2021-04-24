#!/usr/bin/env bash

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD="`which java`"
  fi
fi

$JAVACMD -server -Dfile.encoding=UTF-8 -Djava.awt.headless=true  -Xms10g -Xmx10g -cp ~/pHMLRP/target/pHMLRP.jar com.abdul.Benchmark >app.log 2>runs.log