@echo off
REM ====================================================================
REM SkillPilot — Stop Both Backend and Frontend Services
REM ====================================================================
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop.ps1"
