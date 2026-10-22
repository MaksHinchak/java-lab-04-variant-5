#!/bin/sh
cd "$(dirname "$0")" || exit 1
JDK_LABS="${JAVA_HOME:-/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home}"
if [ -x "$JDK_LABS/bin/javac" ]; then
  JAVAC_LABS="$JDK_LABS/bin/javac"
  JAVA_LABS="$JDK_LABS/bin/java"
else
  JAVAC_LABS=javac
  JAVA_LABS=java
fi
mkdir -p out
"$JAVAC_LABS" -encoding UTF-8 --release 17 -d out src/*.java || exit 1
exec "$JAVA_LABS" -cp out Main "$@"
