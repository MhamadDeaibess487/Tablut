#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
bash scripts/build.sh
find src/test/java -name '*.java' -print | sort > build/test-sources.txt
javac --release 21 -encoding UTF-8 -cp build/classes -d build/test-classes @build/test-sources.txt
tests=(TestModel TestTableauBit TestCommunication TestReseauLocal TestAIEasy TestAIInter TestAIHard TestRules TestPersistence TestAIRegression TestResources)
for test in "${tests[@]}"; do
  java -ea -cp build/Tablut.jar:build/test-classes "tablut.tests.$test"
done
printf '%s test programs passed\n' "${#tests[@]}"
if [[ "${1:-}" == "--gui" ]]; then
  java -ea -cp build/Tablut.jar:build/test-classes tablut.tests.TestGui
fi
