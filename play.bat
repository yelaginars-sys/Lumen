@echo off
chcp 65001 >nul
title Lumen - update and play
cd /d "%~dp0"

echo ============================================
echo   Lumen DLC: update and play
echo ============================================

where git >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Git is not installed!
    pause
    exit /b 1
)

echo [1/3] Updating from GitHub...
git pull --ff-only
if errorlevel 1 (
    echo [WARNING] Could not fast-forward pull automatically.
)

echo [2/3] Setting Java...
if exist "C:\Users\7272~1\.jdks\corretto-21.0.12.1" (
    set "JAVA_HOME=C:\Users\7272~1\.jdks\corretto-21.0.12.1"
)
if "%JAVA_HOME%"=="" (
    for /d %%D in ("%USERPROFILE%\.jdks\*21*") do set "JAVA_HOME=%%D"
)
echo Using JDK: %JAVA_HOME%

echo [3/3] Launching Minecraft...
call gradlew.bat runClient
if errorlevel 1 (
    echo [ERROR] Client closed or failed to launch.
    pause
)

