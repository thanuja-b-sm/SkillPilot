@echo off
REM ====================================================================
REM SkillPilot — Restart Both Backend and Frontend Services
REM ====================================================================
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0restart.ps1"
