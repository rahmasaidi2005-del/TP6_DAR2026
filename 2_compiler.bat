@echo off
rem Compilation des deux projets (si vous n'utilisez pas Eclipse / IntelliJ)
call "%~dp0config_jdk.bat"

cd /d "%~dp0TP6_CorbaBanque_Server"
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src\corbaServer\BanqueServer.java
copy /y src\jndi.properties bin\ >nul

cd /d "%~dp0TP6_CorbaBanque_Client"
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src\corbaClient\BanqueClient.java
copy /y src\jndi.properties bin\ >nul

echo.
echo Compilation terminee (voir les eventuelles erreurs ci-dessus).
pause
