# ====================================================================
# SkillPilot - Start Both Backend (8080) and Frontend (3000)
# ====================================================================
$rootDir = $PSScriptRoot
Write-Host "Launching SkillPilot Services..." -ForegroundColor Cyan

if (-not $env:DB_PASSWORD) {
    $env:DB_PASSWORD = "2005"
}

Write-Host "Starting Spring Boot Backend on http://localhost:8080..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$rootDir\backend'; `$env:DB_PASSWORD='$env:DB_PASSWORD'; .\mvnw.cmd spring-boot:run"

Write-Host "Starting Vite Frontend on http://localhost:3000..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$rootDir\frontend'; npm run dev"

Write-Host "Both services launched in separate windows!" -ForegroundColor Green
Write-Host "Backend:  http://localhost:8080/api/health" -ForegroundColor Gray
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Gray
