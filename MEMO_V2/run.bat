@echo off
REM MEMO_V2 Launcher Script (Windows)
REM Compiles if needed, then runs the application

cd /d "%~dp0"

echo [INFO] Building and running MEMO_V2 ActivityTracker...
mvn clean compile exec:java -Dexec.mainClass=com.memo_v2.Main -DskipTests
pause