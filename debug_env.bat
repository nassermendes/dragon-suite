@echo off
echo Current PATH:
echo %PATH%

echo.
echo Python Installation Check:
where python || echo Python not found in PATH
where pip || echo Pip not found in PATH

echo.
echo Flutter Installation Check:
where flutter || echo Flutter not found in PATH

echo.
echo Current Directory:
cd

echo.
echo Environment Variables:
set

pause
