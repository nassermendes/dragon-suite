# Start the server in test mode
$env:NODE_ENV = "test"
$serverJob = Start-Job -ScriptBlock {
    Set-Location $using:PWD
    npm run test:server
}

# Wait for server to start
Start-Sleep -Seconds 5

try {
    # Run the connection tests
    npx playwright test tests/connections-test.ts --project=chrome
    
    # Show the report without waiting
    $reportJob = Start-Job -ScriptBlock {
        Set-Location $using:PWD
        Start-Process "http://localhost:9323"
        npx playwright show-report
        Start-Sleep -Seconds 2
        Get-Process | Where-Object { $_.MainWindowTitle -like "*Playwright Test Report*" } | Stop-Process
    }
} finally {
    # Stop the server job
    Stop-Job -Job $serverJob
    Remove-Job -Job $serverJob
    
    # Stop the report job if it exists
    if ($reportJob) {
        Stop-Job -Job $reportJob
        Remove-Job -Job $reportJob
    }
}
