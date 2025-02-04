# Dragon Suite

A background Android service that enables video analysis through ChatGPT, controlled entirely through GPT Actions.

## Setup Instructions

### 1. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named "Dragon Suite"
3. Add an Android app with package name `com.example.dragonsuite`
4. Download `google-services.json` and place it in the `android/app` directory
5. Download the Firebase Admin SDK key and save it for the server setup

### 2. Android App Setup
1. Open the project in Android Studio
2. Build and run the app once to register it with Firebase
3. The app will run in the background with no UI
4. Note the FCM token printed in the logs (you'll need this for GPT Actions)

### 3. Server Setup
1. Navigate to the `server` directory
2. Create a `.env` file with:
   ```
   FIREBASE_ADMIN_SDK_PATH=/path/to/your/firebase-admin-sdk.json
   PORT=5000
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Run the server:
   ```bash
   python app.py
   ```

### 4. GPT Actions Setup
1. Create a new GPT in the GPT Store
2. Add the contents of `gpt-actions/openapi.yaml` as an Action
3. Configure the server URL in the Action settings
4. The GPT can now control your Android app!

## Usage

1. Start a chat with your custom GPT
2. Share a video or video URL with the GPT
3. The GPT will:
   - Send the video to your Android app via FCM
   - The app will analyze the video using GPT-4 Vision
   - Results will appear as notifications on your device
   - The GPT will receive and process the results

## Security Notes

- The app uses Android Keystore for secure key storage
- FCM tokens should be kept private
- The server should be deployed with HTTPS
- Add rate limiting in production
- Use a proper database instead of in-memory storage

## Requirements

- Android 8.0 (API 26) or higher
- Python 3.8+ for the server
- Firebase project
- OpenAI API key
- Custom GPT with Actions enabled
