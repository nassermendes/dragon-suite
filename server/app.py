from flask import Flask, request, jsonify
import firebase_admin
from firebase_admin import credentials, messaging
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

app = Flask(__name__)

# Initialize Firebase Admin SDK
cred = credentials.Certificate(os.getenv('FIREBASE_ADMIN_SDK_PATH'))
firebase_admin.initialize_app(cred)

# In-memory device token storage (replace with a database in production)
device_tokens = {}

@app.route('/api/v1/analyze-video', methods=['POST'])
def analyze_video():
    try:
        data = request.json
        device_token = data.get('deviceToken')
        video_uri = data.get('videoUri')
        upload_url = data.get('uploadUrl')

        if not device_token or not video_uri:
            return jsonify({
                'status': 'error',
                'message': 'Missing required parameters'
            }), 400

        # Create FCM message
        message = messaging.Message(
            data={
                'action': 'analyze_video',
                'video_uri': video_uri,
                'upload_url': upload_url or ''
            },
            token=device_token
        )

        # Send message through FCM
        response = messaging.send(message)

        return jsonify({
            'status': 'success',
            'message': f'Message sent successfully: {response}'
        })

    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500

@app.route('/api/v1/device-token', methods=['POST'])
def register_device_token():
    try:
        data = request.json
        device_token = data.get('deviceToken')
        device_id = data.get('deviceId')

        if not device_token or not device_id:
            return jsonify({
                'status': 'error',
                'message': 'Missing required parameters'
            }), 400

        # Store the device token (use a database in production)
        device_tokens[device_id] = device_token

        return jsonify({
            'status': 'success',
            'message': 'Device token registered successfully'
        })

    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.getenv('PORT', 5000)))
