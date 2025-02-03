import os
import subprocess
from pathlib import Path

def download_and_install(url, filename):
    """Download and install using PowerShell"""
    downloads_dir = os.path.expanduser("~\\Downloads")
    file_path = os.path.join(downloads_dir, filename)
    
    # PowerShell command to download
    ps_download = f'''
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri "{url}" -OutFile "{file_path}" -UseBasicParsing
    '''
    
    print(f"Downloading {filename} from {url}")
    try:
        subprocess.run(['powershell', '-Command', ps_download], check=True)
        print(f"Download complete! Saved to {file_path}")
        
        if os.path.exists(file_path):
            print(f"Installing {filename}")
            result = subprocess.run(
                ['msiexec', '/i', file_path, '/quiet', '/norestart'],
                capture_output=True,
                text=True
            )
            if result.returncode != 0:
                print(f"Installation failed with return code {result.returncode}")
                if result.stderr:
                    print(f"Error: {result.stderr}")
            else:
                print(f"Successfully installed {filename}")
    except subprocess.CalledProcessError as e:
        print(f"Failed to download/install {filename}: {e}")
    except Exception as e:
        print(f"Error: {e}")

def main():
    tools = {
        'GitHub CLI': {
            'url': 'https://github.com/cli/cli/releases/download/v2.40.1/gh_2.40.1_windows_amd64.msi',
            'filename': 'gh-cli.msi'
        },
        'Git Extensions': {
            'url': 'https://github.com/gitextensions/gitextensions/releases/download/v5.1.1/GitExtensions-5.1.1.18053-939c0edb.msi',
            'filename': 'git-extensions.msi'
        }
    }
    
    for tool_name, tool_info in tools.items():
        print(f"\nProcessing {tool_name}...")
        download_and_install(tool_info['url'], tool_info['filename'])

if __name__ == '__main__':
    main()
