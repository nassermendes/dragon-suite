import subprocess
import os
from pathlib import Path

def run_git_command(cmd, cwd=None):
    """Run a git command and capture output"""
    try:
        result = subprocess.run(cmd, shell=True, cwd=cwd, capture_output=True, text=True)
        print(f"Command: {cmd}")
        print("Exit code:", result.returncode)
        print("Output:", result.stdout)
        if result.stderr:
            print("Error:", result.stderr)
        return result.returncode == 0
    except Exception as e:
        print(f"Exception running command: {e}")
        return False

def test_git_config():
    """Test git configuration"""
    print("\nTesting git configuration...")
    commands = [
        "git config --list",
        "git remote -v",
        "git status",
        "git branch"
    ]
    
    for cmd in commands:
        print(f"\nRunning: {cmd}")
        run_git_command(cmd)

def test_git_push():
    """Test git push with debug output"""
    print("\nTesting git push...")
    
    # First, try with debug
    print("\nTrying git push with debug...")
    run_git_command("git push --verbose origin main")
    
    # If that fails, try with GCM debug
    print("\nTrying with GCM debug...")
    os.environ['GCM_TRACE'] = '1'
    run_git_command("git push origin main")
    
def main():
    project_root = Path(__file__).parent.parent
    os.chdir(project_root)
    
    print("Starting git push debug tests...")
    test_git_config()
    test_git_push()

if __name__ == '__main__':
    main()
