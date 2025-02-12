import os
from pathlib import Path

def update_env_file():
    root_dir = Path(__file__).parent.parent
    env_file = root_dir / '.env'
    
    env_content = '''# Facebook/Instagram Integration
FACEBOOK_APP_ID=617132931252130
FACEBOOK_APP_SECRET=96e434f663fb7ac24cab858df3184b96

# OpenAI Integration
OPENAI_API_KEY=your_openai_key_here

# Firebase Configuration
FIREBASE_PROJECT_ID=dragon-suite
FIREBASE_PRIVATE_KEY=
FIREBASE_CLIENT_EMAIL=
'''
    
    with open(env_file, 'w') as f:
        f.write(env_content)
    print(f"Updated {env_file} with new Facebook credentials")

if __name__ == "__main__":
    update_env_file()
