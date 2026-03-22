#!/bin/bash
# MEMO_V2 Launcher Script
# Compiles if needed, then runs the application

cd "$(dirname "$0")"

echo "[INFO] Building and running MEMO_V2 ActivityTracker..."
mvn clean compile exec:java -Dexec.mainClass=com.memo_v2.Main -DskipTests
