@echo off
rem Etape 4.3 : lancement du client (apres l'annuaire et le serveur)
call "%~dp0config_jdk.bat"
cd /d "%~dp0TP6_CorbaBanque_Client"
java -cp bin corbaClient.BanqueClient
pause
