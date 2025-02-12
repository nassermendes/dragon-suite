import os
import subprocess
import json
from pathlib import Path

def setup_ci_environment():
    """Setup CI environment variables and configurations"""
    try:
        # Create necessary directories
        os.makedirs('.github/workflows', exist_ok=True)
        os.makedirs('.gitlab', exist_ok=True)
        
        # Install dependencies
        subprocess.run(['pip', 'install', 'firebase-admin', 'google-cloud-storage'], check=True)
        
        # Setup git hooks
        subprocess.run(['git', 'config', 'core.hooksPath', '.github/hooks'], check=True)
        
        return True
    except Exception as e:
        print(f"Error setting up CI environment: {e}")
        return False

def validate_ci_config():
    """Validate CI configuration files"""
    try:
        # Check GitHub Actions workflow
        github_workflow = Path('.github/workflows/ci.yml')
        if github_workflow.exists():
            with open(github_workflow) as f:
                if 'GitGuardian' not in f.read():
                    print("Warning: GitGuardian security scan not found in GitHub Actions workflow")
        
        # Check GitLab CI config
        gitlab_ci = Path('.gitlab-ci.yml')
        if gitlab_ci.exists():
            with open(gitlab_ci) as f:
                if 'security_scan' not in f.read():
                    print("Warning: Security scan not found in GitLab CI configuration")
        
        return True
    except Exception as e:
        print(f"Error validating CI config: {e}")
        return False

def setup_firebase_distribution():
    """Setup Firebase App Distribution"""
    try:
        subprocess.run(['npm', 'install', '-g', 'firebase-tools'], check=True)
        subprocess.run(['firebase', 'login:ci'], check=True)
        
        return True
    except Exception as e:
        print(f"Error setting up Firebase Distribution: {e}")
        return False

if __name__ == '__main__':
    setup_ci_environment()
    validate_ci_config()
    setup_firebase_distribution()
