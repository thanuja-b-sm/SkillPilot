# ====================================================================
# SkillPilot - Start Both Backend (8080) and Frontend (3000)
# ====================================================================
$rootDir = $PSScriptRoot
Write-Host "Launching SkillPilot Services..." -ForegroundColor Cyan

# Ensure Java 17 is used if installed in default path
if (Test-Path "C:\Program Files\Java\jdk-17") {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
    $env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"
}

Write-Host "Starting Spring Boot Backend on http://localhost:8080..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", "cd '$rootDir\backend'; .\start-dev.ps1"

Write-Host "Starting Vite Frontend on http://localhost:3000..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$rootDir\frontend'; npm run dev"

Write-Host "Both services launched in separate windows!" -ForegroundColor Green
Write-Host "Backend:  http://localhost:8080/api/health" -ForegroundColor Gray
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Gray
