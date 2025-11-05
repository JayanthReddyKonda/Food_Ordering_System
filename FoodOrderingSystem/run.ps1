# Food Ordering System - Run Script (PowerShell)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting Food Ordering System" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check if compiled
if (-not (Test-Path "bin\com\foodordering\App.class")) {
    Write-Host "`n❌ Not compiled. Run: .\compile.ps1" -ForegroundColor Red
    exit 1
}

# Run
Write-Host ""
java -cp "bin;lib\*" com.foodordering.App
