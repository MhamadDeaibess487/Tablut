#!/bin/bash

set -e

echo "=== Compilation du projet ==="

rm -rf out
mkdir out

javac -d out \
  src/Config/*.java \
  src/Controleur/*.java \
  src/Model/*.java \
  src/Reseau/*.java \
  src/Utils/*.java \
  src/Vue/*.java \
  src/Tablut.java \
  src/Tests/*.java

echo "Compilation OK"

echo "=== Tests automatiques ==="

java -cp out Tests.TestModel
java -cp out Tests.TestTableauBit
java -cp out Tests.TestCommunication
java -cp out Tests.TestReseauLocal
java -cp out Tests.TestAIEasy
java -cp out Tests.TestAIInter
java -cp out Tests.TestAIHard

echo "Tests automatiques OK"

echo "=== Création du jar ==="

jar cfe Tablut.jar Tablut -C out . -C . res -C . src

echo "Jar créé OK"
echo "Tous les tests sont passés."
