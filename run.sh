#!/bin/sh
# Переходимо в каталог проєкту, щоб відносні шляхи data/ працювали однаково.
cd "$(dirname "$0")" || exit 1
# Спочатку використовуємо JAVA_HOME; на цьому Mac доступний комплект Java всередині IntelliJ.
JDK_LABS="${JAVA_HOME:-/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home}"
# Якщо заданого JDK немає, пробуємо стандартні команди із PATH.
if [ -x "$JDK_LABS/bin/javac" ]; then
  JAVAC_LABS="$JDK_LABS/bin/javac"
  JAVA_LABS="$JDK_LABS/bin/java"
else
  JAVAC_LABS=javac
  JAVA_LABS=java
fi
# --release 17 забезпечує сумісність коду з Java 17 та новішими версіями.
mkdir -p out
"$JAVAC_LABS" -encoding UTF-8 --release 17 -d out src/*.java || exit 1
# Усі аргументи скрипту передаємо в Main без змін.
exec "$JAVA_LABS" -cp out Main "$@"
