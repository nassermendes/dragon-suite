import base64
import json
import os
import subprocess
import sys

def read_file_base64(file_path):
    """Read a file and return its base64 encoded content."""
    try:
        with open(file_path, 'rb') as f:
            return base64.b64encode(f.read()).decode('utf-8')
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")
        return None

def get_firebase_token():
    """Get Firebase CLI token."""
    try:
        # Try to get a CI token
        result = subprocess.run('npx firebase-tools login:ci --no-localhost', 
                              shell=True,
                              capture_output=True, 
                              text=True)
        
        if result.returncode == 0:
            # Extract token from output
            return result.stdout.strip()
        
        print("Error getting Firebase token:")
        print(result.stderr)
        return None
    except Exception as e:
        print(f"Error running Firebase CLI: {e}")
        return None

def main():
    print("🔐 Setting up GitHub Secrets for Dragon Suite")
    print("\nThis script will help you set up the required secrets for GitHub Actions.")
    
    # Check for google-services.json
    google_services_path = os.path.join('android', 'app', 'google-services.json')
    if not os.path.exists(google_services_path):
        print(f"\n❌ {google_services_path} not found!")
        print("Please download it from Firebase Console and place it in the android/app directory.")
        return False
    
    # Check for Firebase service account key
    service_account_path = 'firebase-service-account.json'
    if not os.path.exists(service_account_path):
        print(f"\n❌ {service_account_path} not found!")
        print("Please download it from Firebase Console > Project Settings > Service Accounts")
        return False
    
    # Get Firebase CLI token
    print("\n🔑 Getting Firebase CLI token...")
    firebase_token = get_firebase_token()
    if not firebase_token:
        print("❌ Failed to get Firebase token")
        return False
    
    # Encode files
    google_services = read_file_base64(google_services_path)
    service_account = read_file_base64(service_account_path)
    
    if not all([google_services, service_account]):
        print("❌ Failed to read required files")
        return False
    
    print("\n✅ All required files processed successfully!")
    print("\n📋 Add these secrets to your GitHub repository:")
    print("(Settings > Secrets and variables > Actions > New repository secret)")
    print("\n1. GOOGLE_SERVICES_JSON:")
    print(google_services)
    print("\n2. FIREBASE_SERVICE_ACCOUNT:")
    print(service_account)
    print("\n3. FIREBASE_TOKEN:")
    print(firebase_token)
    
    print("\n🎉 Once you've added these secrets, the automatic distribution workflow will be ready!")
    return True

if __name__ == '__main__':
    success = main()
    sys.exit(0 if success else 1)
