@echo off

set JDK_JMODS=C:\Program Files\Java\jdk-21\jmods
set JFX_JMODS=C:\Users\Administrator\Documents\Librerias de java\javafx-jmods-21.0.10

"C:\Program Files\Java\jdk-21\bin\jpackage" ^
 --type app-image ^
 --name "Gestor de Pulseras" ^
 --input build ^
 --main-jar gestor_de_pulseras.jar ^
 --main-class src.Main ^
 --icon "tangoid.ico" ^
 --app-version 1.1.0 ^
 --vendor "TANGOID" ^
 --dest dist-app ^
 --win-console ^
 --module-path "%JDK_JMODS%;%JFX_JMODS%" ^
 --add-modules javafx.controls,javafx.fxml,javafx.graphics,java.desktop,jdk.charsets ^
 --verbose

pause