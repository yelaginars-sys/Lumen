@echo off
chcp 65001 >nul
title Lumen - update and play
cd /d "%~dp0"

echo ============================================
echo   Lumen: обновление и запуск
echo ============================================

where git >nul 2>nul
if errorlevel 1 (
    echo [ВНИМАНИЕ] Git не установлен! Скачай с https://git-scm.com/download/win
    pause
    exit /b 1
)

echo [1/3] Скачиваю обновления с GitHub...
git pull --ff-only
if errorlevel 1 (
    echo.
    echo [ОШИБКА] Не удалось обновиться автоматически.
    echo Скорее всего есть несохранённые локальные изменения или конфликт.
    echo Запусти: git stash ^&^& git pull
    pause
    exit /b 1
)
echo Обновление завершено.

echo [2/3] Проверяю Java...
if "%JAVA_HOME%"=="" (
    for /d %%D in ("%USERPROFILE%\.jdks\ms-21*") do set "JAVA_HOME=%%D"
    for /d %%D in ("%USERPROFILE%\.jdks\*21*") do set "JAVA_HOME=%%D"
)
if "%JAVA_HOME%"=="" (
    echo [ВНИМАНИЕ] Не найден JDK 21! Установи его через IntelliJ IDEA: File -^> Project Structure -^> SDK
) else (
    echo Использую JDK: %JAVA_HOME%
)

echo [3/3] Запускаю Minecraft...
call gradlew.bat runClient
