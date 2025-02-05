import base64
import json
import subprocess

def read_file_base64(file_path):
    """Read a file and return its base64 encoded content."""
    with open(file_path, 'rb') as f:
        return base64.b64encode(f.read()).decode('utf-8')

def set_secret(name, value):
    """Set a GitHub secret."""
    try:
        cmd = f'gh secret set {name}'
        process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate(input=value.encode())
        if process.returncode == 0:
            print(f"✅ Successfully set {name}")
        else:
            print(f"❌ Failed to set {name}")
            print(f"Error: {stderr.decode()}")
    except Exception as e:
        print(f"❌ Error setting {name}: {e}")

def main():
    # Read and encode files
    google_services = read_file_base64('android/app/google-services.json')
    service_account = read_file_base64('firebase-service-account.json')
    firebase_token = "1//03Ifokq8vu57ZCgYIARAAGAMSNwF-L9IrJApmPtsjTyB9GTMHmGoRsgyo2kLgFYr-GlufEmhcuL8Bd3MClYhhM3NfzX7E3lQVs3o"

    print("🔐 Setting up GitHub secrets...")
    
    # Set secrets
    set_secret('GOOGLE_SERVICES_JSON', google_services)
    set_secret('FIREBASE_SERVICE_ACCOUNT', service_account)
    set_secret('FIREBASE_TOKEN', firebase_token)

if __name__ == '__main__':
    main()
