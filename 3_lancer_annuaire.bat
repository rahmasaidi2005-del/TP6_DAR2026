@echo off
rem Etape 4.1 : lancement de l'annuaire de noms CORBA (port 900)
call "%~dp0config_jdk.bat"
cd /d "%~dp0TP6_CorbaBanque_Server"
echo Annuaire CORBA (Naming Service) - ne fermez pas cette fenetre.
tnameserv -ORBInitialPort 900
pause
