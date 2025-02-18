# Download Python installer
$pythonUrl = "https://www.python.org/ftp/python/3.11.8/python-3.11.8-amd64.exe"
$installerPath = "$env:TEMP\python_installer.exe"

# Download the installer
Invoke-WebRequest -Uri $pythonUrl -OutFile $installerPath

# Install Python
Start-Process -FilePath $installerPath -ArgumentList "/quiet", "InstallAllUsers=1", "PrependPath=1", "Include_test=0" -Wait

# Install pip
python -m ensurepip --upgrade

# Install required packages
pip install virtualenv
pip install python-dotenv
pip install pylint
pip install black
pip install mypy
pip install pytest
pip install pytest-cov

# Install VSCode Python extensions
code --install-extension ms-python.python
code --install-extension ms-python.vscode-pylance
code --install-extension ms-python.black-formatter
code --install-extension ms-python.pylint
code --install-extension ms-python.mypy-type-checker

# Create .env file
$envContent = @"
PYTHON_PATH=$env:USERPROFILE\AppData\Local\Programs\Python\Python311\python.exe
PYTHONPATH=$env:USERPROFILE\CascadeProjects\dragon-suite
FLUTTER_PATH=$env:USERPROFILE\flutter\bin
"@

Set-Content -Path ".env" -Value $envContent

# Refresh environment variables
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "Python installation and setup completed. Please restart your terminal."
