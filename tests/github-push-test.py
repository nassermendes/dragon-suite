import subprocess
import os
import sys
from pathlib import Path

def run_command(cmd):
    """Run a command and return its output"""
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        print(f"\nCommand: {cmd}")
        print("Exit code:", result.returncode)
        print("Output:", result.stdout)
        if result.stderr:
            print("Error:", result.stderr)
        return result.returncode == 0, result.stdout, result.stderr
    except Exception as e:
        print(f"Exception: {e}")
        return False, "", str(e)

def setup_github_cli():
    """Set up GitHub CLI if needed"""
    print("\nChecking GitHub CLI installation...")
    success, _, _ = run_command("gh --version")
    if not success:
        print("Installing GitHub CLI...")
        run_command("winget install --id GitHub.cli")
        return run_command("gh --version")[0]
    return True

def authenticate_github():
    """Authenticate with GitHub"""
    print("\nAuthenticating with GitHub...")
    return run_command("gh auth status")[0]

def push_to_github():
    """Push changes to GitHub"""
    print("\nPushing changes to GitHub...")
    commands = [
        "git add .",
        'git commit -m "feat: add TikTok URL prefix verification file"',
        "gh repo view",  # Verify repository connection
        "gh repo sync"   # Sync with remote
    ]
    
    for cmd in commands:
        success, _, _ = run_command(cmd)
        if not success:
            print(f"Failed to execute: {cmd}")
            return False
    return True

def main():
    project_root = Path(__file__).parent.parent
    os.chdir(project_root)
    
    print("Starting GitHub push test...")
    
    if not setup_github_cli():
        print("Failed to set up GitHub CLI")
        return 1
    
    if not authenticate_github():
        print("Failed to authenticate with GitHub")
        return 1
    
    if not push_to_github():
        print("Failed to push changes")
        return 1
    
    print("\nSuccessfully pushed changes to GitHub!")
    return 0

if __name__ == "__main__":
    sys.exit(main())
