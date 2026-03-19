# SHA-1 Fingerprint Extractor Script
# This script finds keytool and extracts SHA-1 from debug keystore

Write-Host "===================================="
Write-Host "SHA-1 Fingerprint Extractor"
Write-Host "===================================="
Write-Host ""

# Possible keytool locations
$keytoolPaths = @(
    "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe",
    "C:\Program Files\Android\Android Studio\jre\bin\keytool.exe",
    "${env:JAVA_HOME}\bin\keytool.exe"
)

$keytool = $null
foreach ($path in $keytoolPaths) {
    if (Test-Path $path) {
        $keytool = $path
        Write-Host "✓ Found keytool at: $path"
        break
    }
}

if (-not $keytool) {
    Write-Host "✗ Keytool not found in standard locations" -ForegroundColor Red
    Write-Host ""
    Write-Host "MANUAL METHOD:" -ForegroundColor Yellow
    Write-Host "1. In Android Studio, go to: File → Settings → Build, Execution, Deployment → Build Tools → Gradle"
    Write-Host "2. Note the 'Gradle JDK' path"
    Write-Host "3. Open that folder and find: bin\keytool.exe"
    Write-Host "4. Run this command (replace PATH_TO_JDK):"
    Write-Host '   "PATH_TO_JDK\bin\keytool.exe" -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android'
    Write-Host ""
    pause
    exit 1
}

# Check if debug keystore exists
$keystorePath = "$env:USERPROFILE\.android\debug.keystore"
if (-not (Test-Path $keystorePath)) {
    Write-Host "✗ Debug keystore not found at: $keystorePath" -ForegroundColor Red
    Write-Host "Build your app at least once in Android Studio to generate it."
    pause
    exit 1
}

Write-Host "✓ Found debug keystore"
Write-Host ""
Write-Host "Extracting certificates..."
Write-Host ""
Write-Host "===================================="

# Run keytool
& $keytool -list -v -keystore $keystorePath -alias androiddebugkey -storepass android -keypass android

Write-Host "===================================="
Write-Host ""
Write-Host "Look for the SHA1 line above (format: SHA1: XX:XX:XX:...)"
Write-Host "Copy that entire line and add it to Firebase Console"
Write-Host ""
pause
