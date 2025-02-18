@echo off
echo Debugging and fixing paths...

REM Set Python paths
set PYTHON_HOME=%USERPROFILE%\AppData\Local\Programs\Python\Python311
set PYTHON_SCRIPTS=%PYTHON_HOME%\Scripts
set FLUTTER_HOME=%USERPROFILE%\flutter\bin

REM Create backup of current PATH
set PATH_BACKUP=%PATH%

REM Add to system PATH
setx PATH "%PATH_BACKUP%;%PYTHON_HOME%;%PYTHON_SCRIPTS%;%FLUTTER_HOME%" /M

REM Create .env file with proper line endings
echo PYTHON_HOME=%PYTHON_HOME%> .env
echo PYTHON_SCRIPTS=%PYTHON_SCRIPTS%>> .env
echo FLUTTER_HOME=%FLUTTER_HOME%>> .env
echo PATH=%PATH_BACKUP%;%PYTHON_HOME%;%PYTHON_SCRIPTS%;%FLUTTER_HOME%>> .env
echo PYTHONPATH=%CD%>> .env

REM Verify the changes
echo.
echo New PATH set to:
echo %PATH%

echo.
echo Created .env file with contents:
type .env

echo.
echo Please restart your terminal for changes to take effect.
pause
