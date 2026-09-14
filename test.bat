@echo off
echo ========================================================
echo     RUNNING TYPING BATTLE ARENA TEST SUITE
echo ========================================================

call compile.bat
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo Executing headless verification suite...
java -ea -cp bin com.typingbattle.test.GameTestSuite
pause

