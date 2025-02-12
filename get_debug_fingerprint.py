import subprocess
import os

def get_debug_fingerprint():
    keystore_path = os.path.join('android', 'app', 'keystore', 'debug.keystore')
    
    try:
        cmd = [
            'keytool', '-list', '-v',
            '-keystore', keystore_path,
            '-alias', 'androiddebugkey',
            '-storepass', 'android',
            '-keypass', 'android'
        ]
        
        result = subprocess.run(cmd, capture_output=True, text=True)
        
        if result.returncode != 0:
            print("Error running keytool:", result.stderr)
            return None
        
        output_lines = result.stdout.split('\n')
        sha256_line = None
        
        for line in output_lines:
            if 'SHA256:' in line:
                sha256_line = line.strip()
                break
        
        if sha256_line:
            # Extract just the fingerprint part and remove colons
            fingerprint = sha256_line.split('SHA256:')[1].strip().replace(':', '')
            print("SHA256 Fingerprint:", fingerprint)
            return fingerprint
        else:
            print("SHA256 fingerprint not found in output")
            return None
            
    except Exception as e:
        print(f"Error: {e}")
        return None

if __name__ == '__main__':
    fingerprint = get_debug_fingerprint()
    if fingerprint:
        print("\nUse this fingerprint in TikTok Developer Portal:", fingerprint)
