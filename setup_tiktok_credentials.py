import os
import json
import webbrowser
from pathlib import Path

def update_local_properties(client_key, app_id):
    local_props_path = "android/local.properties"
    
    # Read existing properties
    properties = {}
    if os.path.exists(local_props_path):
        with open(local_props_path, 'r') as f:
            for line in f:
                if '=' in line:
                    key, value = line.strip().split('=', 1)
                    properties[key] = value
    
    # Update TikTok properties
    properties['TIKTOK_CLIENT_KEY'] = client_key
    properties['TIKTOK_APP_ID'] = app_id
    
    # Write back to file
    with open(local_props_path, 'w') as f:
        for key, value in properties.items():
            f.write(f"{key}={value}\n")
    
    print(f"Updated {local_props_path} with TikTok credentials")

def update_app_association(app_id):
    association_path = "public/.well-known/tiktok-app-association"
    
    with open(association_path, 'r') as f:
        data = json.load(f)
    
    # Update App ID
    data['applinks']['details'][0]['appID'] = app_id
    
    # Write back to file
    with open(association_path, 'w') as f:
        json.dump(data, f, indent=2)
    
    print(f"Updated {association_path} with App ID")

def main():
    # Ensure we're in the project root
    project_root = Path(__file__).parent
    os.chdir(project_root)
    
    print("TikTok App Registration Helper")
    print("\nStep 1: Opening TikTok Developer Portal...")
    webbrowser.open("https://developers.tiktok.com/")
    
    print("\nFollow these steps:")
    print("1. Sign in to your TikTok account")
    print("2. Create a new app if you haven't already")
    print("3. Set the following in your app configuration:")
    print("   - Package Name: com.example.dragonsuite")
    print("   - App Name: Dragon Suite")
    print(f"   - SHA-256 Certificate: 8d334e4441743a5bb82b5e98797f288d5ddab965a6de4d515ec412ff2a34f997")
    
    print("\nOnce you have created your app, enter the credentials:")
    client_key = input("Client Key: ").strip()
    app_id = input("App ID: ").strip()
    
    if client_key and app_id:
        # Update local.properties
        update_local_properties(client_key, app_id)
        
        # Update tiktok-app-association
        update_app_association(app_id)
        
        print("\nCredentials have been configured successfully!")
        print("\nNext steps:")
        print("1. Build and run your app")
        print("2. Test TikTok authentication")
        print("3. Test deep linking")
    else:
        print("\nError: Both Client Key and App ID are required")

if __name__ == "__main__":
    main()
