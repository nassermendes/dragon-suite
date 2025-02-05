import os
import subprocess
import sys
import json
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
        return True, result.stdout
    except subprocess.CalledProcessError as e:
        print(f"Error executing command: {e}")
        print(f"Output: {e.output}")
        return False, e.output

def get_changed_files():
    """Get list of changed files."""
    _, output = run_command("git status --porcelain")
    changed_files = []
    for line in output.split('\n'):
        if line.strip():
            status, file_path = line[:2], line[3:]
            changed_files.append((status.strip(), file_path.strip()))
    return changed_files

def update_version():
    """Update version numbers in version.json and build.gradle."""
    version_file = 'version.json'
    gradle_file = os.path.join('android', 'app', 'build.gradle')
    
    # Load current version
    try:
        with open(version_file, 'r') as f:
            version_data = json.load(f)
    except FileNotFoundError:
        version_data = {
            "version": {
                "major": 1,
                "minor": 0,
                "patch": 0,
                "build": 0
            },
            "last_update": datetime.utcnow().isoformat() + "Z"
        }
    
    # Increment build number
    version_data["version"]["build"] += 1
    version_data["last_update"] = datetime.utcnow().isoformat() + "Z"
    
    # Save updated version
    with open(version_file, 'w') as f:
        json.dump(version_data, f, indent=4)
    
    # Update build.gradle
    version = version_data["version"]
    version_code = version["build"]
    version_name = f"{version['major']}.{version['minor']}.{version['patch']}"
    
    with open(gradle_file, 'r') as f:
        lines = f.readlines()
    
    for i, line in enumerate(lines):
        if 'versionCode' in line:
            lines[i] = f'        versionCode {version_code}\n'
        elif 'versionName' in line:
            lines[i] = f'        versionName "{version_name}"\n'
    
    with open(gradle_file, 'w') as f:
        f.writelines(lines)
    
    return version_name, version_code

def monitor_build():
    """Monitor the GitHub Actions build status."""
    try:
        result = subprocess.run(
            'gh run list --limit 1 --json status,conclusion,databaseId,url',
            shell=True,
            capture_output=True,
            text=True
        )
        if result.returncode == 0:
            runs = json.loads(result.stdout)
            if runs:
                run = runs[0]
                status = run.get('status', 'unknown')
                conclusion = run.get('conclusion', None)
                url = run.get('url', '')
                
                if status == 'completed':
                    if conclusion == 'success':
                        print("\n✅ Build completed successfully!")
                        print("📱 Check your email for the download link")
                    else:
                        print(f"\n❌ Build failed with conclusion: {conclusion}")
                        print(f"🔍 View details at: {url}")
                else:
                    print(f"\n⏳ Build is still {status}...")
                    print(f"🔍 View progress at: {url}")
            else:
                print("\n❌ No recent builds found")
    except Exception as e:
        print(f"\n❌ Error monitoring build: {e}")

def commit_and_push(message=None):
    """Commit all changes and push to remote."""
    project_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Get changed files
    changed_files = get_changed_files()
    if not changed_files:
        print("No changes to commit!")
        return True
    
    # Print changes
    print("\n📝 Changed files:")
    for status, file_path in changed_files:
        status_map = {
            'M': '🔄 Modified:',
            'A': '➕ Added:',
            'D': '❌ Deleted:',
            '??': '❓ Untracked:'
        }
        status_text = status_map.get(status, f'[{status}]')
        print(f"{status_text} {file_path}")
    
    # Update version numbers
    print("\n📦 Updating version numbers...")
    version_name, version_code = update_version()
    print(f"Version: {version_name} (build {version_code})")
    
    # Generate commit message if not provided
    if not message:
        android_changes = any('android' in file[1] for file in changed_files)
        python_changes = any(file[1].endswith('.py') for file in changed_files)
        
        changes = []
        if android_changes:
            changes.append("Android app updates")
        if python_changes:
            changes.append("Python script updates")
        if not changes:
            changes.append("General updates")
        
        message = f"feat: {', '.join(changes)} (v{version_name}-{version_code})\n\n"
        message += "Changes:\n"
        for _, file_path in changed_files:
            message += f"- {file_path}\n"
    
    # Commit changes
    print(f"\n📦 Committing changes...")
    success, _ = run_command("git add .")
    if not success:
        return False
    
    success, _ = run_command(f'git commit -m "{message}"')
    if not success:
        return False
    
    # Push changes
    print("\n🚀 Pushing to remote...")
    success, _ = run_command("git push origin main")
    if not success:
        return False
    
    print("\n✅ Changes pushed successfully!")
    print("⏳ GitHub Actions will build and distribute the app automatically.")
    print("📧 Check your email for the download link once the build is complete.")
    
    # Monitor build status
    print("\n🔍 Monitoring build status...")
    monitor_build()
    
    return True

if __name__ == "__main__":
    message = sys.argv[1] if len(sys.argv) > 1 else None
    success = commit_and_push(message)
    sys.exit(0 if success else 1)
