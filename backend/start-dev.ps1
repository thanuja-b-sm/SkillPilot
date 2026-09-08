# SkillPilot Backend — Development Startup Script
# Usage: .\start-dev.ps1
# Reads backend/.env and starts Spring Boot with those env vars set.

$envFile = Join-Path $PSScriptRoot ".env"

if (-not (Test-Path $envFile)) {
    Write-Error "'.env' file not found. Copy '.env.example' to '.env' and fill in your values."
    exit 1
}

# Ensure Java 17 is used if installed in default path
if (Test-Path "C:\Program Files\Java\jdk-17") {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
    $env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"
    [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Process")
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
        # Strip surrounding matching quotes if present
        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        if ($value -eq "") {
            Remove-Item -Path "env:$key" -ErrorAction SilentlyContinue
        } else {
            Set-Item -Path "env:$key" -Value $value -ErrorAction SilentlyContinue
        }
        Write-Host "  SET $key" -ForegroundColor DarkGray
    }
}

Write-Host ""
Write-Host "Starting Spring Boot backend on port $env:PORT ..." -ForegroundColor Green
Write-Host ""

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
