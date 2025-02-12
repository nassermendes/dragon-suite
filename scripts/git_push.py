import os
import subprocess
from pathlib import Path

def run_git_command(command):
    try:
        result = subprocess.run(
            command,
            shell=True,
            check=True,
            capture_output=True,
            text=True
        )
        print(f"Output: {result.stdout}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error: {e.stderr}")
        return False

def main():
    # Set git config
    run_git_command('git config --global credential.helper manager-core')
    
    # Add changes
    run_git_command('git add .')
    
    # Commit changes
    run_git_command('git commit -m "security: clean up repository and update security configurations" --no-verify')
    
    # Force push changes
    success = run_git_command('git push origin main --force')
    
    return 0 if success else 1

if __name__ == "__main__":
    os.chdir(Path(__file__).parent.parent)
    exit(main())
