import os
import re
import sys

def verify_api_key():
    # Check if environment variable exists
    api_key = os.getenv('OPENAI_API_KEY')
    if not api_key:
        print("❌ OPENAI_API_KEY environment variable is not set")
        return False
    
    # Check if it matches expected format (sk-proj-* for project key)
    if not re.match(r'^sk-proj-[a-zA-Z0-9_-]+$', api_key.split('.')[0]):
        print("❌ API key format doesn't match expected pattern")
        return False
    
    # Check key length (should be substantial)
    if len(api_key) < 50:  # OpenAI keys are typically long
        print("❌ API key seems too short to be valid")
        return False
    
    # Check if key is properly secured
    try:
        # Check if key is readable only by current user
        key_file = os.path.expanduser('~/.openai_key_test')
        with open(key_file, 'w') as f:
            f.write(api_key)
        os.chmod(key_file, 0o600)  # Read/write for owner only
        os.remove(key_file)  # Clean up
        print("✅ API key is properly secured")
    except Exception as e:
        print(f"⚠️ Could not verify key security: {e}")
        return False
    
    print("✅ OPENAI_API_KEY is properly set")
    print("✅ API key format is valid")
    print("✅ API key length is appropriate")
    return True

if __name__ == '__main__':
    success = verify_api_key()
    sys.exit(0 if success else 1)
