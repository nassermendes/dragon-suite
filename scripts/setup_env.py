import os
from pathlib import Path

def create_env_file():
    root_dir = Path(__file__).parent.parent
    env_file = root_dir / '.env'
    
    env_content = '''# Facebook/Instagram Integration
FACEBOOK_APP_ID=
FACEBOOK_APP_SECRET=

# OpenAI Integration
OPENAI_API_KEY=your_openai_key_here

# Firebase Configuration
FIREBASE_PROJECT_ID=dragon-suite
FIREBASE_PRIVATE_KEY=
FIREBASE_CLIENT_EMAIL=
'''
    
    with open(env_file, 'w') as f:
        f.write(env_content)
    print(f"Created {env_file}")

def create_local_properties():
    root_dir = Path(__file__).parent.parent
    local_props_file = root_dir / 'android' / 'app' / 'local.properties'
    
    # Ensure android/app directory exists
    local_props_file.parent.mkdir(parents=True, exist_ok=True)
    
    local_props_content = f'''sdk.dir={os.path.expanduser('~')}\\AppData\\Local\\Android\\Sdk

# API Keys
OPENAI_API_KEY=your_openai_key_here
TIKTOK_CLIENT_KEY=
TIKTOK_APP_ID=

# Social Media Tokens
INSTAGRAM_TOKEN_MENDES=
INSTAGRAM_TOKEN_CHARITY=
YOUTUBE_TOKEN_MENDES=
YOUTUBE_TOKEN_CHARITY=
TIKTOK_TOKEN_MENDES=
TIKTOK_TOKEN_CHARITY=
'''
    
    with open(local_props_file, 'w') as f:
        f.write(local_props_content)
    print(f"Created {local_props_file}")

if __name__ == "__main__":
    create_env_file()
    create_local_properties()
    print("Environment files created successfully!")
