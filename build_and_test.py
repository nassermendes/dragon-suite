import os
import subprocess
import sys
from pathlib import Path

def run_command(cmd, cwd=None):
    try:
        result = subprocess.run(cmd, cwd=cwd, shell=True, text=True, capture_output=True)
        print("Output:", result.stdout)
        if result.stderr:
            print("Errors:", result.stderr)
        return result.returncode == 0
    except Exception as e:
        print(f"Error running command: {e}")
        return False

def main():
    project_root = Path(__file__).parent
    android_dir = project_root / "android"
    
    print("Building debug APK...")
    if not run_command("gradlew.bat assembleDebug", cwd=android_dir):
        print("Failed to build debug APK")
        return False
    
    print("\nGetting app signatures...")
    if not run_command("python tests/tiktok_config_test.py", cwd=project_root):
        print("Failed to get app signatures")
        return False
    
    return True

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
