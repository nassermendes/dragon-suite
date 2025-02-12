import os
import sys
import requests
import zipfile
from pathlib import Path

def download_sdk():
    """Download TikTok SDK from their developer portal"""
    print("Downloading TikTok SDK...")
    
    # Create libs directory if it doesn't exist
    libs_dir = Path("android/app/libs")
    libs_dir.mkdir(parents=True, exist_ok=True)
    
    # SDK URLs
    sdk_files = {
        "tiktok-open-sdk-1.1.0.aar": "https://developers.tiktok.com/sdk/android/tiktok-open-sdk-1.1.0.aar"
    }
    
    for filename, url in sdk_files.items():
        target_path = libs_dir / filename
        if not target_path.exists():
            try:
                print(f"Downloading {filename}...")
                response = requests.get(url)
                response.raise_for_status()
                
                with open(target_path, 'wb') as f:
                    f.write(response.content)
                print(f"Downloaded {filename}")
            except Exception as e:
                print(f"Failed to download {filename}: {e}")
                return False
    
    return True

def update_build_gradle():
    """Update build.gradle to use local SDK"""
    print("Updating build.gradle...")
    
    app_gradle = Path("android/app/build.gradle")
    with open(app_gradle, 'r') as f:
        content = f.read()
    
    # Add local repository
    if 'flatDir {' not in content:
        repositories_section = content.find('repositories {')
        if repositories_section != -1:
            insert_pos = content.find('}', repositories_section)
            repository_config = """
        flatDir {
            dirs 'libs'
        }
"""
            new_content = content[:insert_pos] + repository_config + content[insert_pos:]
            content = new_content
    
    # Update dependencies
    if 'com.tiktok.open.sdk:tiktok-open-sdk' in content:
        # Replace existing TikTok SDK dependency
        lines = content.split('\n')
        new_lines = []
        for line in lines:
            if 'com.tiktok.open.sdk:tiktok-open-sdk' in line:
                new_lines.append('    implementation(name: "tiktok-open-sdk-1.1.0", ext: "aar")')
            else:
                new_lines.append(line)
        content = '\n'.join(new_lines)
    else:
        # Add new TikTok SDK dependency
        dependencies_section = content.find('dependencies {')
        if dependencies_section != -1:
            insert_pos = content.find('}', dependencies_section)
            dependencies_config = """
    implementation(name: "tiktok-open-sdk-1.1.0", ext: "aar")
"""
            content = content[:insert_pos] + dependencies_config + content[insert_pos:]
    
    with open(app_gradle, 'w') as f:
        f.write(content)
    
    print("Updated build.gradle")
    return True

def main():
    # Change to project root
    project_root = Path(__file__).parent
    os.chdir(project_root)
    
    print("Starting TikTok SDK setup...")
    
    if download_sdk() and update_build_gradle():
        print("TikTok SDK setup completed successfully!")
        return 0
    else:
        print("TikTok SDK setup failed!")
        return 1

if __name__ == "__main__":
    sys.exit(main())
