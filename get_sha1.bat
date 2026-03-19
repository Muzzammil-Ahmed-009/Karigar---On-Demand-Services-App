@echo off
echo Looking for keytool...
echo.

set KEYTOOL=""

REM Check common Android Studio locations
if exist "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" (
    set KEYTOOL="C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe"
    goto :found
)

if exist "C:\Program Files\Android\Android Studio\jre\bin\keytool.exe" (
    set KEYTOOL="C:\Program Files\Android\Android Studio\jre\bin\keytool.exe"
    goto :found
)

if exist "%JAVA_HOME%\bin\keytool.exe" (
    set KEYTOOL="%JAVA_HOME%\bin\keytool.exe"
    goto :found
)

echo Keytool not found in default locations.
echo.
echo Please run this command in Android Studio Terminal:
echo ./gradlew signingReport
echo.
pause
exit /b 1

:found
echo Found keytool at: %KEYTOOL%
echo.
echo Generating SHA-1 fingerprint...
echo.
echo ============================================
%KEYTOOL% -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
echo ============================================
echo.
echo Copy the SHA1 value above and add it to Firebase Console
echo.
pause
