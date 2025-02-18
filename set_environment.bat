@echo off
echo Setting up environment variables...

REM Define paths
set PYTHON_DIR=%LOCALAPPDATA%\Programs\Python\Python311
set PYTHON_SCRIPTS=%PYTHON_DIR%\Scripts
set FLUTTER_DIR=%USERPROFILE%\flutter\bin
set PROJECT_DIR=%CD%

REM Create .env file
echo Creating .env file...
(
echo PYTHON_HOME=%PYTHON_DIR%
echo PYTHON_SCRIPTS=%PYTHON_SCRIPTS%
echo FLUTTER_HOME=%FLUTTER_DIR%
echo PYTHONPATH=%PROJECT_DIR%
echo PATH=%PYTHON_DIR%;%PYTHON_SCRIPTS%;%FLUTTER_DIR%;%PATH%
) > %PROJECT_DIR%\.env

REM Update system PATH
echo Updating system PATH...
setx PATH "%PYTHON_DIR%;%PYTHON_SCRIPTS%;%FLUTTER_DIR%;%PATH%" /M

echo Environment setup complete.
echo Please restart your terminal for changes to take effect.
timeout /t 5
