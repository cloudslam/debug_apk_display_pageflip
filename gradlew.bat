@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo Gradle is required to build this project because binary wrapper files are intentionally not checked in. 1>&2
echo Install Gradle, then run: gradlew assembleDebug 1>&2
exit /b 1
