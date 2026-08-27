@echo off
cd /d "%~dp0"
chcp 65001 > nul
java -jar CineMax.jar
pause