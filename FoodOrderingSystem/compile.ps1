# Food Ordering System - Simple Compile Script (PowerShell)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Compiling Food Ordering System" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check Java
Write-Host "`nChecking Java..." -ForegroundColor Yellow
try {
    java -version 2>&1 | Out-Null
    Write-Host "✓ Java found" -ForegroundColor Green
} catch {
    Write-Host "❌ Java not found" -ForegroundColor Red
    exit 1
}

# Create bin directory
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Check for MySQL Connector
$mysqlJar = Get-ChildItem -Path "lib" -Filter "mysql-connector-*.jar" -ErrorAction SilentlyContinue
if (-not $mysqlJar) {
    Write-Host "❌ MySQL Connector JAR not found in lib/" -ForegroundColor Red
    Write-Host "Download from: https://dev.mysql.com/downloads/connector/j/" -ForegroundColor Yellow
    exit 1
}

# Compile
Write-Host "`nCompiling..." -ForegroundColor Yellow
javac -cp "lib\*" -d bin src\com\foodordering\*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful!" -ForegroundColor Green
    Write-Host "`nRun with: .\run.ps1" -ForegroundColor Yellow
} else {
    Write-Host "❌ Compilation failed" -ForegroundColor Red
    exit 1
}
