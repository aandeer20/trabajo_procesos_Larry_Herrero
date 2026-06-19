@echo off
echo Iniciando DeustoRestaurant...
cd /d "%~dp0"
call .\gradlew.bat bootRun
pause
