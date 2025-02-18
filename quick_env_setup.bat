@echo off
echo Setting up Python and Flutter environment...

REM Set paths directly
setx PYTHON_HOME "%LOCALAPPDATA%\Programs\Python\Python311" /M
setx PATH "%LOCALAPPDATA%\Programs\Python\Python311;%LOCALAPPDATA%\Programs\Python\Python311\Scripts;%USERPROFILE%\flutter\bin;%PATH%" /M

REM Create minimal .env
echo PYTHON_HOME=%LOCALAPPDATA%\Programs\Python\Python311> .env
echo PATH=%PATH%>> .env
echo PYTHONPATH=%CD%>> .env
echo FLUTTER_HOME=%USERPROFILE%\flutter\bin>> .env

echo Setup complete. Please restart your terminal.
timeout /t 3
