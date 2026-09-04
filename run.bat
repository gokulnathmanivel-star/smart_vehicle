@echo off
title SVS-BAS Application Launcher
echo ===================================================================
echo  Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
echo  Launching Full-Stack Application...
echo ===================================================================

echo.
echo [1/2] Launching Spring Boot Backend on Port 8080...
start "SVS-BAS Backend (Port 8080)" cmd /k "cd /d %~dp0 && maven\bin\mvn.cmd spring-boot:run -Dspring-boot.run.profiles=h2"

echo.
echo [2/2] Launching Frontend Web Server on Port 5500...
start "SVS-BAS Frontend (Port 5500)" powershell -ExecutionPolicy Bypass -File "%~dp0frontend\start-server.ps1" -Port 5500

echo.
echo ===================================================================
echo  Both services launched in separate windows!
echo  - Frontend URL : http://localhost:5500/login.html
echo  - Backend API  : http://localhost:8080/api/v1
echo  - Swagger UI   : http://localhost:8080/api/v1/swagger-ui/index.html
echo ===================================================================
echo.
