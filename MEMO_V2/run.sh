#!/bin/bash
# MEMO_V2 Launcher Script
# Compiles if JAR is absent, then runs the application

cd "$(dirname "$0")"

JAR_FILE="target/MEMO_V2-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "[INFO] JAR not found ($JAR_FILE). Building project..."
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "[ERROR] Build failed. Check output above."
        exit 1
    fi
fi

echo "[INFO] Starting MEMO_V2 ActivityTracker..."
java -jar "$JAR_FILE"
