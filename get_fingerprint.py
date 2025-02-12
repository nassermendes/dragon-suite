import subprocess
import os
import base64
import hashlib

def get_keystore_fingerprint():
    keystore_path = os.path.join('android', 'app', 'debug.keystore')
    if not os.path.exists(keystore_path):
        print(f"Keystore not found at {keystore_path}")
        return None
    
    try:
        # Export the certificate
        export_cmd = [
            'keytool', '-exportcert',
            '-alias', 'androiddebugkey',
            '-keystore', keystore_path,
            '-storepass', 'android'
        ]
        cert_data = subprocess.check_output(export_cmd)
        
        # Calculate SHA-256
        sha256_hash = hashlib.sha256(cert_data).digest()
        
        # Convert to base64
        base64_hash = base64.b64encode(sha256_hash).decode('utf-8')
        
        print("SHA-256 fingerprint:", base64_hash)
        return base64_hash
        
    except subprocess.CalledProcessError as e:
        print("Error executing keytool:", e)
        return None
    except Exception as e:
        print("Error:", e)
        return None

if __name__ == '__main__':
    get_keystore_fingerprint()
