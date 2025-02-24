@echo off
setx PATH "%PATH%;%USERPROFILE%\AppData\Local\Programs\Python\Python311;%USERPROFILE%\AppData\Local\Programs\Python\Python311\Scripts;%USERPROFILE%\flutter\bin"

echo Creating .env file...
(
echo PYTHON_HOME=%USERPROFILE%\AppData\Local\Programs\Python\Python311
echo PYTHON_SCRIPTS=%USERPROFILE%\AppData\Local\Programs\Python\Python311\Scripts
echo PATH=%PATH%
echo PYTHONPATH=%USERPROFILE%\CascadeProjects\dragon-suite
echo FLUTTER_PATH=%USERPROFILE%\flutter\bin
) > .env

echo Paths added and .env file created.
echo Please restart your terminal for changes to take effect.
pause
