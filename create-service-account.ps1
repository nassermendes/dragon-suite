# Create service account for key rotation
gcloud iam service-accounts create key-rotation-sa `
    --display-name="Service Account for Key Rotation"

# Grant necessary permissions
gcloud projects add-iam-policy-binding dragon-suite `
    --member="serviceAccount:key-rotation-sa@dragon-suite.iam.gserviceaccount.com" `
    --role="roles/iam.serviceAccountKeyAdmin"

# Create and download key
gcloud iam service-accounts keys create key-rotation-sa-key.json `
    --iam-account=key-rotation-sa@dragon-suite.iam.gserviceaccount.com

# Set up environment variable
$env:GOOGLE_APPLICATION_CREDENTIALS="$PWD\key-rotation-sa-key.json"
