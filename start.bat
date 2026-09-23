@echo off
title Turf-Ballers Server
echo ========================================================
echo               ⚽ Turf-Ballers Web Server
echo ========================================================
echo Starting local web server on http://localhost:3000 ...
echo.
start /b powershell -ExecutionPolicy Bypass -File "%~dp0serve.ps1" -Port 3000
timeout /t 2 /nobreak >nul
echo Opening Turf-Ballers in your browser...
start http://localhost:3000/login.html
echo.
echo Website running at: http://localhost:3000
echo Press Ctrl+C in this window or close it to stop the server.
powershell -Command "while($true){ Start-Sleep -Seconds 1 }"
