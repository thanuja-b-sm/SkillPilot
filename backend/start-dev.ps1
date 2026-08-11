# SkillPilot Backend — Development Startup Script
# Usage: .\start-dev.ps1
# Reads backend/.env and starts Spring Boot with those env vars set.

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Error "'.env' file not found. Copy '.env.example' to '.env' and fill in your values."
    exit 1
}

Write-Host "Loading environment from .env ..." -ForegroundColor Cyan

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    # Skip blank lines and comments
    if ($line -eq "" -or $line.StartsWith("#")) { return }
    $parts = $line -split "=", 2
    if ($parts.Length -eq 2) {
        $key   = $parts[0].Trim()
        $value = $parts[1].Trim()
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  SET $key" -ForegroundColor DarkGray
    }
}

Write-Host ""
Write-Host "Starting Spring Boot backend on port $env:PORT ..." -ForegroundColor Green
Write-Host ""

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
