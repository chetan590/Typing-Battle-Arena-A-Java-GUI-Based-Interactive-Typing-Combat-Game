@echo off
title Typing Battle Arena
echo ========================================================
echo        STARTING TYPING BATTLE ARENA
echo ========================================================

if not exist bin\com\typingbattle\Main.class (
    echo Bin directory not found or outdated. Compiling first...
    call compile.bat
)

echo Launching Typing Battle Arena...
java -cp bin com.typingbattle.Main

