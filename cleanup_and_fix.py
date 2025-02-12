import os
import shutil
import json
import subprocess
from pathlib import Path

def run_command(command, cwd=None):
    if sys.platform == "win32":
        command = command.replace("./gradlew", "gradlew")
    process = subprocess.run(command, shell=True, cwd=cwd, capture_output=True, text=True)
    return process.returncode == 0, process.stdout, process.stderr

def clean_build_directories():
    """Clean build directories and temporary files"""
    print("Cleaning build directories...")
    
    dirs_to_clean = [
        ".gradle",
        "build",
        "android/.gradle",
        "android/build",
        "android/app/build",
        "node_modules",
        "__pycache__",
        ".pytest_cache",
        "test-results",
        "playwright-report"
    ]
    
    for dir_path in dirs_to_clean:
        if os.path.exists(dir_path):
            print(f"Removing {dir_path}")
            shutil.rmtree(dir_path)

def update_build_gradle():
    """Update build.gradle files with correct configurations"""
    print("Updating build.gradle files...")
    
    # Update project build.gradle
    project_gradle = "android/build.gradle"
    with open(project_gradle, 'r') as f:
        content = f.read()
    
    # Update repositories
    if 'maven { url "https://artifact.bytedance.com/repository/tiktok" }' not in content:
        repositories_section = content.find('repositories {')
        if repositories_section != -1:
            insert_pos = content.find('}', repositories_section)
            repository_config = """
        maven { url "https://artifact.bytedance.com/repository/tiktok" }
        maven { url "https://storage.googleapis.com/download.flutter.io" }
        maven { url 'https://jitpack.io' }
        maven { url 'https://maven.google.com' }
"""
            new_content = content[:insert_pos] + repository_config + content[insert_pos:]
            with open(project_gradle, 'w') as f:
                f.write(new_content)
    
    # Update app build.gradle
    app_gradle = "android/app/build.gradle"
    with open(app_gradle, 'r') as f:
        content = f.read()
    
    # Update dependencies
    if 'com.tiktok.open.sdk:tiktok-open-sdk' not in content:
        dependencies_section = content.find('dependencies {')
        if dependencies_section != -1:
            insert_pos = content.find('}', dependencies_section)
            dependencies_config = """
    // TikTok SDK
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk:1.1.0'
"""
            new_content = content[:insert_pos] + dependencies_config + content[insert_pos:]
            with open(app_gradle, 'w') as f:
                f.write(new_content)

def update_local_properties():
    """Update local.properties with SDK path"""
    print("Updating local.properties...")
    
    local_props = "android/local.properties"
    if not os.path.exists(local_props):
        android_home = os.environ.get('ANDROID_HOME')
        if not android_home:
            android_home = os.path.expanduser('~/AppData/Local/Android/Sdk')
        
        with open(local_props, 'w') as f:
            f.write(f"sdk.dir={android_home.replace(os.sep, '/')}\n")
            f.write("TIKTOK_CLIENT_KEY=awtcv2pehk3juw5v\n")
            f.write("TIKTOK_APP_ID=V3YdcqktUiAzLEYpGFr1t6AZHaHe2CyT\n")

def clean_and_build():
    """Clean and rebuild the project"""
    print("Cleaning and rebuilding project...")
    
    android_dir = "android"
    run_command("gradlew clean", cwd=android_dir)
    success, stdout, stderr = run_command("gradlew assembleDebug", cwd=android_dir)
    
    if success:
        print("Build successful!")
    else:
        print("Build failed:")
        print("stdout:", stdout)
        print("stderr:", stderr)

def main():
    # Change to project root
    project_root = Path(__file__).parent
    os.chdir(project_root)
    
    print("Starting cleanup and fix process...")
    
    # Clean directories
    clean_build_directories()
    
    # Update configuration files
    update_build_gradle()
    update_local_properties()
    
    # Clean and build
    clean_and_build()
    
    print("Cleanup and fix process completed!")

if __name__ == "__main__":
    import sys
    main()
