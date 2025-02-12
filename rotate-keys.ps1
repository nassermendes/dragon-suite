# Set environment variable for authentication
$env:GOOGLE_APPLICATION_CREDENTIALS="$PWD\key-rotation-sa-key.json"

# List all service accounts
$accounts = gcloud iam service-accounts list --format="value(email)"

foreach ($account in $accounts) {
    # List existing keys
    $keys = gcloud iam service-accounts keys list --iam-account=$account --format="value(name)"
    
    # Create new key
    gcloud iam service-accounts keys create "$account-new-key.json" --iam-account=$account
    
    # Delete oldest key if more than 2 exist
    $keyCount = ($keys | Measure-Object -Line).Lines
    if ($keyCount -gt 1) {
        $oldestKey = $keys | Select-Object -Last 1
        gcloud iam service-accounts keys delete $oldestKey --iam-account=$account --quiet
    }
    
    Write-Output "Rotated keys for $account"
}
