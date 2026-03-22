@echo off
REM MEMO_V2 Launcher Script (Windows)
REM Compiles if needed, then runs the application

cd /d "%~dp0"

echo [INFO] Building MEMO_V2 ActivityTracker...
mvn clean compile -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed. Check output above.
    pause
    exit /b 1
)

echo [INFO] Build successful!
echo [INFO] To run the GUI, execute: mvn exec:java -Dexec.mainClass=com.memo_v2.Main
echo [INFO] (Requires X11 DISPLAY variable for Swing UI)
pause