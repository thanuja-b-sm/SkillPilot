# ====================================================================
# SkillPilot - Restart Both Backend and Frontend Servers
# ====================================================================
$rootDir = $PSScriptRoot

& "$rootDir\stop.ps1"

Write-Host "Waiting 2 seconds for ports to fully clear..." -ForegroundColor Gray
Start-Sleep -Seconds 2

& "$rootDir\start.ps1"
