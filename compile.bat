@echo off
echo ========================================================
echo   COMPILING TYPING BATTLE ARENA SOURCE CODE
echo ========================================================

if not exist bin mkdir bin

echo Compiling Java classes to bin/...
javac -encoding UTF-8 -d bin src\com\typingbattle\model\*.java src\com\typingbattle\engine\*.java src\com\typingbattle\data\*.java src\com\typingbattle\ui\*.java src\com\typingbattle\test\*.java src\com\typingbattle\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check JDK installation and errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation completed cleanly!

