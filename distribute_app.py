import os
import subprocess
import sys
from datetime import datetime

def run_command(command, cwd=None):
    """Run a command and return its output."""
    try:
        result = subprocess.run(
            command,
            cwd=cwd,
            shell=True,
            check=True,
            capture_output=True,
            text=True
        )
        print(result.stdout)
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e}")
        print(f"Output: {e.output}")
        return False

def distribute_app(build_type="debug", version_bump=True):
    """Build and distribute the app via Firebase App Distribution."""
    project_dir = os.path.dirname(os.path.abspath(__file__))
    android_dir = os.path.join(project_dir, "android")
    
    print("\n🚀 Starting app distribution process...")
    
    if version_bump:
        # TODO: Implement version bumping logic
        print("\n📦 Version bump skipped (to be implemented)")
    
    # Clean project
    print("\n🧹 Cleaning project...")
    if not run_command("./gradlew clean", cwd=android_dir):
        return False
    
    # Build the app
    print(f"\n🔨 Building {build_type} APK...")
    build_task = f"assemble{build_type.capitalize()}"
    if not run_command(f"./gradlew {build_task}", cwd=android_dir):
        return False
    
    # Distribute via Firebase
    print("\n📱 Distributing to Firebase...")
    distribute_task = f"appDistribution{build_type.capitalize()}"
    if not run_command(f"./gradlew {distribute_task}", cwd=android_dir):
        return False
    
    print("\n✅ Distribution completed successfully!")
    print("\nNext steps:")
    print("1. Check your email for the download link")
    print("2. Install the new version on your device")
    print("3. The app will automatically check for updates on launch")
    
    return True

if __name__ == "__main__":
    build_type = "debug" if len(sys.argv) < 2 else sys.argv[1].lower()
    if build_type not in ["debug", "release"]:
        print("Error: Build type must be 'debug' or 'release'")
        sys.exit(1)
    
    success = distribute_app(build_type)
    sys.exit(0 if success else 1)
