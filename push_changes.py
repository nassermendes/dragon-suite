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
        
        message = f"feat: {', '.join(changes)}\n\n"
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
    return True

if __name__ == "__main__":
    message = sys.argv[1] if len(sys.argv) > 1 else None
    success = commit_and_push(message)
    sys.exit(0 if success else 1)
