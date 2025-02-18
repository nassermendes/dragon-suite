# Add Python paths
$pythonPath = "$env:USERPROFILE\AppData\Local\Programs\Python\Python311"
$pythonScriptsPath = "$pythonPath\Scripts"

# Get current user PATH
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")

# Add Python paths if they don't exist
if (-not $userPath.Contains($pythonPath)) {
    $userPath = "$userPath;$pythonPath"
}
if (-not $userPath.Contains($pythonScriptsPath)) {
    $userPath = "$userPath;$pythonScriptsPath"
}

# Update user PATH
[Environment]::SetEnvironmentVariable("Path", $userPath, "User")

# Create .env file
$envContent = @"
PYTHON_HOME=$pythonPath
PYTHON_SCRIPTS=$pythonScriptsPath
PATH=$userPath
PYTHONPATH=$env:USERPROFILE\CascadeProjects\dragon-suite
FLUTTER_PATH=$env:USERPROFILE\flutter\bin
"@

Set-Content -Path ".env" -Value $envContent

Write-Host "Python paths added to PATH and .env file created. Please restart your terminal."
