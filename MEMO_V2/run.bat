@echo off
REM MEMO_V2 Launcher Script (Windows)
REM Compiles if JAR is absent, then runs the application

cd /d "%~dp0"

set JAR_FILE=target\MEMO_V2-1.0-SNAPSHOT.jar

if not exist "%JAR_FILE%" (
    echo [INFO] JAR not found (%JAR_FILE%). Building project...
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo [ERROR] Build failed. Check output above.
        pause
        exit /b 1
    )
)

echo [INFO] Starting MEMO_V2 ActivityTracker...
java -jar "%JAR_FILE%"
pause