#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
rm -rf -- build/classes build/test-classes
mkdir -p build/classes build/test-classes
find src/main/java -name '*.java' -print | sort > build/main-sources.txt
javac --release 21 -encoding UTF-8 -d build/classes @build/main-sources.txt
cp -R src/main/resources/. build/classes/
jar --create --file build/Tablut.jar --main-class tablut.Tablut -C build/classes .
printf 'Built build/Tablut.jar (Java 21)\n'
