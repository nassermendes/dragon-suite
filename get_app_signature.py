import os
import hashlib
from pathlib import Path

def get_app_signature():
    # First, ensure we have a debug keystore
    keystore_dir = Path('android/app/keystore')
    keystore_path = keystore_dir / 'debug.keystore'
    
    if not keystore_path.exists():
        print("Debug keystore not found!")
        return None
    
    try:
        # Calculate MD5 hash of the keystore
        with open(keystore_path, 'rb') as f:
            md5_hash = hashlib.md5(f.read()).hexdigest()
        print("App signature (MD5):", md5_hash)
        return md5_hash
    except Exception as e:
        print(f"Error getting app signature: {e}")
        return None

if __name__ == '__main__':
    signature = get_app_signature()
    if signature:
        print("\nUse this app signature in TikTok Developer Portal:", signature)
