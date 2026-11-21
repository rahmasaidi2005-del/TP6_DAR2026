@echo off
call "%~dp0config_jdk.bat"
echo.
java -version
echo.
where idlj
where tnameserv
echo.
echo Si idlj et tnameserv sont affiches ci-dessus, le JDK 8 est bien configure.
pause
