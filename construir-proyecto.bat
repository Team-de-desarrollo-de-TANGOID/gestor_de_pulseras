@echo off
setlocal ENABLEDELAYEDEXPANSION

set JFX_SDK=C:\Users\Administrator\Documents\Librerias de java\javafx-sdk-21.0.10\lib
set APP_NAME=gestor_de_pulseras
set MAIN_CLASS=src.Main
set SRC_JAVA=src
set OUT_DIR=out
set BUILD_DIR=build

echo ============================================
echo  TANGOID - BUILD JAR
echo ============================================

echo [1/5] Limpiando carpetas previas...
if exist "%OUT_DIR%" rmdir /S /Q "%OUT_DIR%"
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%OUT_DIR%"
mkdir "%BUILD_DIR%"

echo [2/5] Compilando codigo Java...
set FILES=
for /R "%SRC_JAVA%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)

javac -encoding UTF-8 ^
 --module-path "%JFX_SDK%" ^
 --add-modules javafx.controls,javafx.fxml ^
 -d "%OUT_DIR%" !FILES!

if errorlevel 1 (
    echo.
    echo ERROR compilando Java
    pause
    exit /b 1
)

echo [3/5] Copiando recursos...
if not exist "%OUT_DIR%\src" mkdir "%OUT_DIR%\src"
copy "%SRC_JAVA%\*.fxml" "%OUT_DIR%\src\" >nul 2>nul
copy "%SRC_JAVA%\*.css" "%OUT_DIR%\src\" >nul 2>nul
copy "%SRC_JAVA%\*.png" "%OUT_DIR%\src\" >nul 2>nul

echo [4/5] Generando JAR ejecutable...
echo Main-Class: %MAIN_CLASS%> manifest.mf
jar cfm "%BUILD_DIR%\%APP_NAME%.jar" manifest.mf -C "%OUT_DIR%" .
del manifest.mf

if errorlevel 1 (
    echo.
    echo ERROR creando JAR
    pause
    exit /b 1
)

echo [5/5] BUILD COMPLETADO CON EXITO
echo JAR generado en: %BUILD_DIR%\%APP_NAME%.jar
pause