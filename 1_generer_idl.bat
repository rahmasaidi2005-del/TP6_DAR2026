@echo off
rem Etape 2.3 et 3.3 : compilation de Banque.idl (serveur puis client)
call "%~dp0config_jdk.bat"

cd /d "%~dp0TP6_CorbaBanque_Server\src"
echo === Projet SERVEUR ===
idlj -fall -v Banque.idl

cd /d "%~dp0TP6_CorbaBanque_Client\src"
echo.
echo === Projet CLIENT ===
idlj -fall -v Banque.idl

echo.
echo Termine. Les classes sont generees dans le package corbaBanque de chaque projet.
pause
