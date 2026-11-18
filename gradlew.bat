@echo off
rem Minimal Gradle launcher for Remapper project
where gradle >nul 2>nul
if %ERRORLEVEL% equ 0 (
    gradle %*
    exit /b %ERRORLEVEL%
)

echo Gradle is required to build Remapper. Please install Gradle or open in Android Studio.
exit /b 1
