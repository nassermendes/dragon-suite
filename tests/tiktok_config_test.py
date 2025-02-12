import os
import subprocess
import hashlib
import base64
from pathlib import Path

def get_app_signature():
    """Get MD5 hex digest of the debug APK"""
    apk_path = Path('android/app/build/outputs/apk/debug/app-debug.apk')
    if not apk_path.exists():
        print(f"Debug APK not found at {apk_path}")
        return None
    
    try:
        with open(apk_path, 'rb') as f:
            md5_hash = hashlib.md5(f.read()).hexdigest()
        print("App signature (MD5):", md5_hash)
        return md5_hash
    except Exception as e:
        print("Error getting app signature:", e)
        return None

def get_sha256_fingerprint():
    """Get SHA-256 fingerprint of debug keystore"""
    keystore_path = Path('android/app/debug.keystore')
    if not keystore_path.exists():
        print(f"Debug keystore not found at {keystore_path}")
        return None
    
    try:
        # Export certificate and get SHA-256 fingerprint
        cmd = [
            'keytool',
            '-list',
            '-v',
            '-keystore', str(keystore_path),
            '-alias', 'androiddebugkey',
            '-storepass', 'android'
        ]
        output = subprocess.check_output(cmd, text=True)
        
        # Extract SHA-256 fingerprint
        for line in output.split('\n'):
            if 'SHA256:' in line:
                fingerprint = line.split('SHA256:')[1].strip()
                # Format fingerprint to match TikTok's requirements
                fingerprint = fingerprint.replace(':', '').lower()
                print("SHA-256 fingerprint:", fingerprint)
                return fingerprint
        
        print("SHA-256 fingerprint not found in output")
        return None
    except Exception as e:
        print("Error getting SHA-256 fingerprint:", e)
        return None

def main():
    print("Testing TikTok configuration...")
    
    # Get app signature
    print("\nGetting app signature...")
    app_sig = get_app_signature()
    
    # Get SHA-256 fingerprint
    print("\nGetting SHA-256 fingerprint...")
    sha256_fp = get_sha256_fingerprint()
    
    if app_sig and sha256_fp:
        print("\nAll signatures retrieved successfully!")
        print("\nUse these values in TikTok Developer Portal:")
        print("1. App signature:", app_sig)
        print("2. SHA-256 fingerprint:", sha256_fp)
        return True
    else:
        print("\nFailed to get all required signatures")
        return False

if __name__ == '__main__':
    main()
