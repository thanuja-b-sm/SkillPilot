# ====================================================================
# SkillPilot - Stop Both Backend & Frontend Servers
# ====================================================================
Write-Host "Stopping SkillPilot services..." -ForegroundColor Yellow

try {
    $backendConns = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    if ($backendConns) {
        $backendPids = $backendConns | Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($p in $backendPids) {
            if ($p -and $p -ne 0) {
                Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
                Write-Host "Stopped Spring Boot backend (PID: $p)" -ForegroundColor Green
            }
        }
    } else {
        Write-Host "No process running on port 8080." -ForegroundColor Gray
    }
} catch {
    Write-Host "Port 8080 check completed." -ForegroundColor Gray
}

try {
    $frontendConns = Get-NetTCPConnection -LocalPort 3000, 5173 -ErrorAction SilentlyContinue
    if ($frontendConns) {
        $frontendPids = $frontendConns | Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($p in $frontendPids) {
            if ($p -and $p -ne 0) {
                Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
                Write-Host "Stopped Frontend server (PID: $p)" -ForegroundColor Green
            }
        }
    } else {
        Write-Host "No process running on port 3000 / 5173." -ForegroundColor Gray
    }
} catch {
    Write-Host "Port 3000/5173 check completed." -ForegroundColor Gray
}

Write-Host "All SkillPilot services stopped." -ForegroundColor Cyan
