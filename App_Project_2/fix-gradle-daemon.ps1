# Script to fix Gradle daemon JAVA_HOME issue
# This will stop all Gradle daemons and clear the daemon registry

Write-Host "Stopping Gradle daemons..." -ForegroundColor Green

# Stop all Gradle daemons
if (Test-Path ".\gradlew.bat") {
    & ".\gradlew.bat" --stop 2>&1 | Out-Null
    Write-Host "Gradle daemons stopped" -ForegroundColor Green
} else {
    Write-Host "Gradle wrapper not found, trying global gradle..." -ForegroundColor Yellow
    try {
        gradle --stop 2>&1 | Out-Null
    } catch {
        Write-Host "Could not stop daemons (this is okay if Gradle is not in PATH)" -ForegroundColor Yellow
    }
}

# Clear Gradle daemon registry
$gradleUserHome = "$env:USERPROFILE\.gradle"
$daemonRegistry = Join-Path $gradleUserHome "daemon"

if (Test-Path $daemonRegistry) {
    Write-Host "Clearing Gradle daemon registry..." -ForegroundColor Green
    Remove-Item -Path "$daemonRegistry\*.lock" -Force -ErrorAction SilentlyContinue
    Write-Host "Daemon registry cleared" -ForegroundColor Green
} else {
    Write-Host "Gradle daemon registry not found (this is okay)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Fix applied! Please:" -ForegroundColor Green
Write-Host "1. Close Android Studio completely" -ForegroundColor Yellow
Write-Host "2. Reopen Android Studio" -ForegroundColor Yellow
Write-Host "3. File -> Invalidate Caches -> Invalidate and Restart" -ForegroundColor Yellow
Write-Host "4. Sync project again" -ForegroundColor Yellow

