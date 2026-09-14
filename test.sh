#!/bin/sh
# Працюємо від кореня поточного проєкту.
cd "$(dirname "$0")" || exit 1
# Використовуємо заданий JAVA_HOME або Java, що постачається з IntelliJ на цьому Mac.
JDK_LABS="${JAVA_HOME:-/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home}"
if [ -x "$JDK_LABS/bin/javac" ]; then
  JAVAC_LABS="$JDK_LABS/bin/javac"
  JAVA_LABS="$JDK_LABS/bin/java"
else
  JAVAC_LABS=javac
  JAVA_LABS=java
fi
# Компілюємо програму разом з автономними перевірками.
mkdir -p out/tests
"$JAVAC_LABS" -encoding UTF-8 --release 17 -d out/tests src/*.java tests/*.java || exit 1
# Для тестів Swing не потрібен: перевіряємо відокремлені моделі.
exec "$JAVA_LABS" -Djava.awt.headless=true -cp out/tests LabChecks
