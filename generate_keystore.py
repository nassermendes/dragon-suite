import os
import subprocess
import json
from pathlib import Path

def run_command(command, cwd=None):
    process = subprocess.run(command, shell=True, cwd=cwd, capture_output=True, text=True)
    if process.returncode != 0:
        print(f"Error executing command: {command}")
        print(f"Error: {process.stderr}")
        return None
    return process.stdout.strip()

def generate_keystore():
    keystore_path = "android/app/release.keystore"
    alias = "dragon_suite"
    
    # Generate keystore if it doesn't exist
    if not os.path.exists(keystore_path):
        print("Generating release keystore...")
        keytool_cmd = (
            f'keytool -genkey -v -keystore {keystore_path} -alias {alias} '
            f'-keyalg RSA -keysize 2048 -validity 10000 '
            f'-dname "CN=Dragon Suite,OU=Dragon Suite,O=Dragon Suite,L=Unknown,ST=Unknown,C=US" '
            f'-storepass dragon_suite -keypass dragon_suite'
        )
        result = run_command(keytool_cmd)
        if result is None:
            return None
        
    # Get SHA-256 fingerprint
    print("Getting SHA-256 fingerprint...")
    fingerprint_cmd = (
        f'keytool -list -v -keystore {keystore_path} '
        f'-alias {alias} -storepass dragon_suite'
    )
    output = run_command(fingerprint_cmd)
    if output is None:
        return None
    
    # Extract SHA-256 fingerprint
    for line in output.split('\n'):
        if "SHA256:" in line:
            fingerprint = line.split("SHA256:")[1].strip()
            fingerprint = fingerprint.replace(':', '').lower()
            return fingerprint
    
    return None

def update_assetlinks(fingerprint):
    assetlinks_path = "public/.well-known/assetlinks.json"
    with open(assetlinks_path, 'r') as f:
        data = json.load(f)
    
    # Update fingerprint
    data[0]['target']['sha256_cert_fingerprints'] = [fingerprint]
    
    # Write back to file
    with open(assetlinks_path, 'w') as f:
        json.dump(data, f, indent=2)
    
    print(f"Updated {assetlinks_path} with fingerprint")

def main():
    # Ensure we're in the project root
    project_root = Path(__file__).parent
    os.chdir(project_root)
    
    # Generate keystore and get fingerprint
    fingerprint = generate_keystore()
    if fingerprint:
        print(f"\nSHA-256 Fingerprint: {fingerprint}")
        update_assetlinks(fingerprint)
        print("\nNext steps:")
        print("1. Add the following to android/app/build.gradle under android.signingConfigs:")
        print("""    release {
        storeFile file('release.keystore')
        storePassword 'dragon_suite'
        keyAlias 'dragon_suite'
        keyPassword 'dragon_suite'
    }""")
        print("\n2. Update the build type in android/app/build.gradle:")
        print("""    buildTypes {
        release {
            signingConfig signingConfigs.release
            ...
        }
    }""")
    else:
        print("Failed to generate keystore or get fingerprint")

if __name__ == "__main__":
    main()
