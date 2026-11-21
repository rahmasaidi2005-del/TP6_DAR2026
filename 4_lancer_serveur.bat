@echo off
rem Etape 4.2 : lancement du serveur (apres l'annuaire)
call "%~dp0config_jdk.bat"
cd /d "%~dp0TP6_CorbaBanque_Server"
java -cp bin corbaServer.BanqueServer
pause
