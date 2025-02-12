import subprocess
import re
from pathlib import Path

def get_debug_keystore_path():
    """Get the path to debug.keystore"""
    # Common locations for debug.keystore
    possible_paths = [
        Path.home() / '.android' / 'debug.keystore',
        Path('android/app/debug.keystore'),
        Path('android/debug.keystore')
    ]
    
    for path in possible_paths:
        if path.exists():
            return str(path)
    
    return None

def get_sha256_fingerprint():
    """Get SHA256 fingerprint from debug keystore"""
    keystore_path = get_debug_keystore_path()
    if not keystore_path:
        print("Debug keystore not found!")
        return None
    
    try:
        cmd = f'keytool -list -v -keystore "{keystore_path}" -alias androiddebugkey -storepass android -keypass android'
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        
        if result.returncode != 0:
            print("Error running keytool:", result.stderr)
            return None
        
        # Find SHA256 fingerprint
        sha256_match = re.search(r'SHA256: ((?:[0-9A-F]{2}:){31}[0-9A-F]{2})', result.stdout)
        if sha256_match:
            # Format fingerprint for TikTok (remove colons and convert to lowercase)
            fingerprint = sha256_match.group(1).replace(':', '').lower()
            print(f"SHA256 fingerprint: {fingerprint}")
            return fingerprint
        else:
            print("SHA256 fingerprint not found in keytool output")
            return None
            
    except Exception as e:
        print(f"Error: {e}")
        return None

def get_app_signature():
    """Get app signature from debug APK"""
    apk_path = Path('android/app/build/outputs/apk/debug/app-debug.apk')
    if not apk_path.exists():
        print("Debug APK not found! Please build the app first.")
        return None
    
    try:
        import hashlib
        with open(apk_path, 'rb') as f:
            md5_hash = hashlib.md5(f.read()).hexdigest()
        print(f"App signature (MD5): {md5_hash}")
        return md5_hash
    except Exception as e:
        print(f"Error getting app signature: {e}")
        return None

if __name__ == '__main__':
    print("Getting TikTok integration signatures...")
    print("\n1. SHA256 Fingerprint:")
    sha256 = get_sha256_fingerprint()
    
    print("\n2. App Signature:")
    signature = get_app_signature()
    
    if sha256 and signature:
        print("\nUse these values in TikTok Developer Portal:")
        print(f"SHA256 Fingerprint: {sha256}")
        print(f"App Signature: {signature}")
    else:
        print("\nFailed to get all required signatures")
