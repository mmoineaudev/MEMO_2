@echo off
REM MEMO_V2 Launcher Script (Windows)
REM Compiles and runs the application

cd /d "%~dp0"

echo [INFO] Building MEMO_V2 ActivityTracker...
call mvn clean compile -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed. Check output above.
    pause
    exit /b 1
)

echo [INFO] Build successful! Starting application...
call mvn exec:java -Dexec.mainClass=com.memo_v2.Main
