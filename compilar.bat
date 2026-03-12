@echo off
title Lanzador TangoID - Diagnostico

set JFX_SDK=C:\Users\Administrator\Documents\Librerias de java\javafx-sdk-21.0.10\lib

echo [1/3] Limpiando binarios...
if exist bin rmdir /s /q bin
mkdir bin

echo [2/3] Compilando archivos Java...
javac --module-path "%JFX_SDK%" --add-modules javafx.controls,javafx.fxml -d bin -encoding UTF-8 src\*.java

if %errorlevel% neq 0 (
    echo [ERROR] Error de compilacion.
    pause
    exit /b 1
)

echo [3/3] Sincronizando recursos (FXML, CSS, PNG)...
if not exist bin\src mkdir bin\src
copy src\*.fxml bin\src\ >nul
copy src\*.css bin\src\ >nul
copy src\*.png bin\src\ >nul

echo [OK] Iniciando aplicacion en modo consola...
java --module-path "%JFX_SDK%" --add-modules javafx.controls,javafx.fxml -cp bin src.Main

echo.
echo El programa se ha cerrado. Revisa los errores arriba.
pause