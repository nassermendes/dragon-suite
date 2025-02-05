# Dragon Suite Setup Guide

## One-Time Setup

1. **Firebase Service Account**:
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Select your project
   - Go to Project Settings > Service Accounts
   - Click "Generate New Private Key"
   - Save the file as `firebase-service-account.json` in the project root

2. **Firebase App Distribution**:
   - Go to Firebase Console > App Distribution
   - Click "Get Started"
   - Add your email as a tester
   - Create a group called "testers"

3. **GitHub Secrets**:
   - Run `python setup_github_secrets.py`
   - Copy the three secrets it provides
   - Go to your GitHub repository
   - Navigate to Settings > Secrets and variables > Actions
   - Add each secret:
     - `GOOGLE_SERVICES_JSON`
     - `FIREBASE_SERVICE_ACCOUNT`
     - `FIREBASE_TOKEN`

## Daily Development

To push changes and trigger automatic builds:

```bash
# Simple push with auto-generated commit message
python push_changes.py

# Push with custom commit message
python push_changes.py "Your commit message here"
```

The script will:
1. Show you what files changed
2. Commit all changes
3. Push to GitHub
4. Trigger the build pipeline
5. You'll receive an email when the new version is ready

## Testing the App

1. Install the latest version:
   - Check your email for the download link
   - Click to install the new version
   - The app will check for updates on launch

2. Key Features:
   - Video recording and upload
   - ChatGPT integration
   - Background processing
   - Push notifications

## Troubleshooting

If you encounter issues:
1. Check the GitHub Actions tab for build errors
2. Verify Firebase configuration
3. Make sure all secrets are properly set
4. Check your email for Firebase App Distribution notifications
