import subprocess, time

# Start logcat process with explicit encoding and error handling
pro = subprocess.Popen([
    r'C:\Users\nasse\AppData\Local\Android\Sdk\platform-tools\adb.exe', 'logcat', '-v', 'time'
], stdout=subprocess.PIPE, encoding='utf-8', errors='replace')

print('Waiting for FCM Token...')

timeout = time.time() + 60  # 60 seconds timeout
found = False

while time.time() < timeout:
    line = pro.stdout.readline()
    if 'FCM Token:' in line:
        print('Found:', line.strip())
        found = True
        break

if not found:
    print('FCM Token not found')
