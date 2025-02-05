import http.server
import socketserver
import os
from urllib.parse import quote

PORT = 8000
APK_PATH = "android/app/build/outputs/apk/debug/app-debug.apk"

class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            # Serve the APK file
            try:
                with open(APK_PATH, 'rb') as f:
                    self.send_response(200)
                    self.send_header('Content-Type', 'application/vnd.android.package-archive')
                    self.send_header('Content-Disposition', f'attachment; filename="{os.path.basename(APK_PATH)}"')
                    self.end_headers()
                    self.wfile.write(f.read())
            except Exception as e:
                self.send_response(500)
                self.end_headers()
                self.wfile.write(str(e).encode())
        else:
            super().do_GET()

def get_local_ip():
    import socket
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # Doesn't need to be reachable
        s.connect(('10.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP

def main():
    local_ip = get_local_ip()
    url = f"http://{local_ip}:{PORT}"
    print(f"\n🚀 Starting APK server at {url}")
    print("\n📱 To install the app:")
    print("1. Make sure your phone is on the same WiFi network as this computer")
    print(f"2. Open this URL on your phone: {url}")
    print("3. Download and install the APK")
    print("\n⚠️ Press Ctrl+C to stop the server\n")
    
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        print("Server started...")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down server...")
            httpd.shutdown()

if __name__ == '__main__':
    main()
