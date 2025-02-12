import os
import subprocess
import sys

def run_command(command):
    process = subprocess.Popen(
        command,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        shell=True
    )
    stdout, stderr = process.communicate()
    return process.returncode, stdout.decode(), stderr.decode()

def main():
    # Configure git
    run_command('git config --global user.name "Nasser Mendes"')
    run_command('git config --global user.email "nassermendes@users.noreply.github.com"')
    
    # Set up SSH key if needed
    ssh_key_path = os.path.expanduser('~/.ssh/id_rsa')
    if not os.path.exists(ssh_key_path):
        print("Generating SSH key...")
        run_command(f'ssh-keygen -t rsa -b 4096 -C "nassermendes@users.noreply.github.com" -f {ssh_key_path} -N ""')
    
    # Start SSH agent and add key
    run_command('eval "$(ssh-agent -s)"')
    run_command(f'ssh-add {ssh_key_path}')
    
    # Push changes
    code, out, err = run_command('git push origin main')
    print(out)
    if err:
        print(f"Error: {err}", file=sys.stderr)
    
    return code

if __name__ == "__main__":
    sys.exit(main())
