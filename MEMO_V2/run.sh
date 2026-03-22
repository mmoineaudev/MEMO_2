#!/bin/bash
# MEMO_V2 Launcher Script
# Compiles if needed, then runs the application

cd "$(dirname "$0")"

echo "[INFO] Building MEMO_V2 ActivityTracker..."
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed. Check output above."
    exit 1
fi

echo "[INFO] Build successful!"
echo "[INFO] To run the GUI, execute: mvn exec:java -Dexec.mainClass=com.memo_v2.Main"
echo "[INFO] (Requires X11 DISPLAY variable for Swing UI)"
