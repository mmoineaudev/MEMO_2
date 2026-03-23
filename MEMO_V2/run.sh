#!/bin/bash
# MEMO_V2 Launcher Script
# Compiles and runs the application

cd "$(dirname "$0")"

echo "[INFO] Building MEMO_V2 ActivityTracker..."
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed. Check output above."
    exit 1
fi

echo "[INFO] Build successful! Starting application..."
mvn exec:java -Dexec.mainClass=com.memo_v2.Main
