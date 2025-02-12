import os
import sys
import json
import subprocess
from pathlib import Path

def run_command(command, cwd=None):
    if sys.platform == "win32":
        command = command.replace("./gradlew", "gradlew")
    process = subprocess.run(command, shell=True, cwd=cwd, capture_output=True, text=True)
    return process.returncode == 0, process.stdout, process.stderr

def test_android_build():
    # Change to project root
    project_root = Path(__file__).parent.parent
    android_dir = project_root / "android"
    
    print("Testing Android build...")
    
    # Clean the project
    success, stdout, stderr = run_command("gradlew clean", cwd=android_dir)
    if not success:
        print("Failed to clean project:")
        print("stdout:", stdout)
        print("stderr:", stderr)
        return False
    
    # Build debug APK
    success, stdout, stderr = run_command("gradlew assembleDebug", cwd=android_dir)
    if not success:
        print("Failed to build debug APK:")
        print("stdout:", stdout)
        print("stderr:", stderr)
        return False
    
    print("Android build successful!")
    return True

def test_tiktok_integration():
    print("Testing TikTok integration...")
    
    # Check TikTok configuration files
    project_root = Path(__file__).parent.parent
    
    # Check domain verification
    domain_verify = project_root / "public" / "tiktok-domain-verify.html"
    if not domain_verify.exists():
        print("Missing tiktok-domain-verify.html")
        return False
    
    # Check app association files
    assetlinks = project_root / "public" / ".well-known" / "assetlinks.json"
    if not assetlinks.exists():
        print("Missing assetlinks.json")
        return False
    
    tiktok_association = project_root / "public" / ".well-known" / "tiktok-app-association"
    if not tiktok_association.exists():
        print("Missing tiktok-app-association")
        return False
    
    # Check if files are properly configured
    with open(assetlinks) as f:
        assetlinks_data = json.load(f)
        if "YOUR_APP_SIGNING_CERTIFICATE_SHA256_FINGERPRINT" in json.dumps(assetlinks_data):
            print("assetlinks.json not configured with actual fingerprint")
            return False
    
    with open(tiktok_association) as f:
        association_data = json.load(f)
        if "YOUR_TIKTOK_APP_ID" in json.dumps(association_data):
            print("tiktok-app-association not configured with actual App ID")
            return False
    
    print("TikTok integration configuration verified!")
    return True

def test_android_manifest():
    print("Testing Android Manifest...")
    
    project_root = Path(__file__).parent.parent
    manifest_file = project_root / "android" / "app" / "src" / "main" / "AndroidManifest.xml"
    
    if not manifest_file.exists():
        print("Missing AndroidManifest.xml")
        return False
    
    with open(manifest_file) as f:
        manifest_content = f.read()
        
        # Check for required activities
        required_activities = [
            "TikTokTestActivity",
            "com.tiktok.open.sdk.auth.AuthActivity"
        ]
        
        for activity in required_activities:
            if activity not in manifest_content:
                print(f"Missing activity in manifest: {activity}")
                return False
    
    print("Android Manifest configuration verified!")
    return True

def main():
    tests = [
        test_android_build,
        test_tiktok_integration,
        test_android_manifest
    ]
    
    success = True
    for test in tests:
        if not test():
            success = False
            break
    
    if success:
        print("\nAll tests passed successfully!")
        return 0
    else:
        print("\nTests failed!")
        return 1

if __name__ == "__main__":
    sys.exit(main())
