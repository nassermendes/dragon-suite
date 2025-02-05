import os
import sys
import time
import json
import random
import requests
import sqlite3
from datetime import datetime, timedelta
from collections import deque
from threading import Lock
from openai import OpenAI

# Rate limiting configuration
class RateLimiter:
    def __init__(self, max_requests=60, time_window=60):
        self.max_requests = max_requests
        self.time_window = time_window  # in seconds
        self.requests = deque()
        self.lock = Lock()
    
    def can_make_request(self):
        now = datetime.now()
        with self.lock:
            # Remove old requests
            while self.requests and (now - self.requests[0]).total_seconds() > self.time_window:
                self.requests.popleft()
            
            # Check if we can make a new request
            if len(self.requests) < self.max_requests:
                self.requests.append(now)
                return True
            return False
    
    def wait_for_slot(self):
        while not self.can_make_request():
            time.sleep(1)

class APIUsageMonitor:
    def __init__(self, db_path="api_usage.db"):
        self.db_path = db_path
        self.setup_database()
    
    def setup_database(self):
        with sqlite3.connect(self.db_path) as conn:
            conn.execute("""
                CREATE TABLE IF NOT EXISTS api_calls (
                    id INTEGER PRIMARY KEY,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    endpoint TEXT,
                    model TEXT,
                    tokens_used INTEGER,
                    success BOOLEAN,
                    error_type TEXT,
                    response_time FLOAT
                )
            """)
    
    def log_call(self, endpoint, model=None, tokens_used=0, success=True, error_type=None, response_time=0):
        with sqlite3.connect(self.db_path) as conn:
            conn.execute(
                "INSERT INTO api_calls (endpoint, model, tokens_used, success, error_type, response_time) VALUES (?, ?, ?, ?, ?, ?)",
                (endpoint, model, tokens_used, success, error_type, response_time)
            )
    
    def get_usage_stats(self, hours=24):
        with sqlite3.connect(self.db_path) as conn:
            conn.row_factory = sqlite3.Row
            cutoff = datetime.now() - timedelta(hours=hours)
            return conn.execute("""
                SELECT 
                    COUNT(*) as total_calls,
                    SUM(CASE WHEN success THEN 1 ELSE 0 END) as successful_calls,
                    AVG(response_time) as avg_response_time,
                    SUM(tokens_used) as total_tokens
                FROM api_calls 
                WHERE timestamp > ?
            """, (cutoff,)).fetchone()

def parse_error_details(error_str):
    """Parse detailed information from error message."""
    print("\n🔍 Error Analysis:")
    
    if 'insufficient_quota' in error_str:
        print("• Issue: Quota exceeded")
        print("• Required Action: Check billing status and limits")
        print("• Recommendation: Visit platform.openai.com/account/billing")
        print("\nPossible Solutions:")
        print("1. Set up billing information")
        print("2. Check for any payment issues")
        print("3. Review and increase quota limits if needed")
    elif '429' in error_str:
        print("• Issue: Rate limit reached")
        print("• Required Action: Implement rate limiting or wait")
        print("• Recommendation: Reduce request frequency")
        print("\nPossible Solutions:")
        print("1. Implement exponential backoff")
        print("2. Reduce concurrent requests")
        print("3. Consider upgrading your plan")
    elif 'invalid_request_error' in error_str:
        print("• Issue: Invalid request parameters")
        print("• Required Action: Check API request format")
        print("• Recommendation: Verify model availability and parameters")
    elif 'model_not_found' in error_str:
        print("• Issue: Model not available")
        print("• Required Action: Check model availability")
        print("• Recommendation: Use a different model")
        print("\nPossible Solutions:")
        print("1. Update to latest model versions")
        print("2. Check model access permissions")
        print("3. Use an available alternative model")
    
    # Extract any JSON error details
    try:
        error_start = error_str.find('{')
        if error_start != -1:
            error_json = json.loads(error_str[error_start:])
            if 'error' in error_json:
                print("\n📝 Detailed Error Info:")
                print(f"• Type: {error_json['error'].get('type', 'unknown')}")
                print(f"• Code: {error_json['error'].get('code', 'unknown')}")
                print(f"• Message: {error_json['error'].get('message', 'unknown')}")
    except:
        pass

def check_model_compatibility(client):
    """Check compatibility of different models and their capabilities."""
    print("\n🔄 Model Compatibility Check:")
    
    try:
        # Get list of available models first
        models_response = client.models.list()
        available_models = [model.id for model in models_response.data]
        print(f"Found {len(available_models)} available models")
        
        # Group models by type
        model_groups = {
            "GPT": [],
            "DALL-E": [],
            "Other": []
        }
        
        for model in available_models:
            if model.startswith(('gpt-', 'text-')):
                model_groups["GPT"].append(model)
            elif 'dall-e' in model.lower():
                model_groups["DALL-E"].append(model)
            else:
                model_groups["Other"].append(model)
        
        # Print available models by group
        for group, models in model_groups.items():
            if models:
                print(f"\n{group} Models:")
                for model in sorted(models):
                    try:
                        model_info = client.models.retrieve(model)
                        print(f"✓ {model}")
                        if hasattr(model_info, 'max_tokens'):
                            print(f"  • Max tokens: {model_info.max_tokens}")
                    except Exception as e:
                        print(f"❌ {model}: {str(e)}")
        
        return available_models
    
    except Exception as e:
        print(f"❌ Error listing models: {str(e)}")
        return []

def test_minimal_api_call(client, monitor, rate_limiter):
    """Test the most minimal possible API call with rate limiting."""
    print("\n🔄 Testing Minimal API Call:")
    
    rate_limiter.wait_for_slot()
    
    # Try different models in order of preference
    models_to_try = [
        "gpt-4o-mini",  # Try the mini model first
        "o1-mini-2024-09-12",  # Fall back to o1-mini
        "gpt-4o-mini-2024-07-18"  # Last resort
    ]
    
    for model in models_to_try:
        try:
            print(f"\nTrying model: {model}")
            start_time = datetime.now()
            
            response = client.chat.completions.create(
                model=model,
                messages=[{"role": "user", "content": "."}],
                max_tokens=1,
                temperature=0
            )
            duration = (datetime.now() - start_time).total_seconds()
            
            monitor.log_call(
                endpoint="chat.completions",
                model=model,
                tokens_used=1,
                success=True,
                response_time=duration
            )
            
            print(f"✓ API call successful with {model}!")
            print(f"Response time: {duration:.2f}s")
            print(f"Response: {response.choices[0].message.content}")
            return True
            
        except Exception as e:
            error_str = str(e)
            monitor.log_call(
                endpoint="chat.completions",
                model=model,
                success=False,
                error_type=error_str
            )
            print(f"❌ Failed with {model}:")
            parse_error_details(error_str)
            print("\nTrying next model...")
    
    print("\n❌ All models failed")
    return False

def verify_api_key(api_key):
    """Verify API key format and configuration."""
    print("\n🔑 API Key Verification:")
    
    if not api_key:
        print("❌ API key is not set")
        return False
    
    # Check key format
    if not api_key.startswith(('sk-', 'sk-proj-')):
        print("❌ Invalid key format - should start with 'sk-' or 'sk-proj-'")
        return False
    
    # Check key length
    if len(api_key) < 40:
        print("❌ Key length is suspiciously short")
        return False
    
    print(f"✓ Key format valid: {api_key[:8]}...{api_key[-8:]}")
    print(f"✓ Key length: {len(api_key)} characters")
    return True

def check_api_endpoints(client, monitor):
    """Test various API endpoints for accessibility."""
    print("\n🌐 API Endpoint Tests:")
    
    endpoints = [
        {
            "name": "Models List",
            "func": lambda: client.models.list()
        },
        {
            "name": "Model Info",
            "func": lambda: client.models.retrieve("gpt-3.5-turbo")
        }
    ]
    
    all_success = True
    for endpoint in endpoints:
        try:
            print(f"\nTesting {endpoint['name']}...")
            start_time = datetime.now()
            response = endpoint["func"]()
            duration = (datetime.now() - start_time).total_seconds()
            
            monitor.log_call(
                endpoint=endpoint["name"],
                success=True,
                response_time=duration
            )
            
            print(f"✓ Success ({duration:.2f}s)")
            
            if endpoint["name"] == "Models List":
                print("Available models:")
                for model in response.data[:5]:
                    print(f"  • {model.id}")
        except Exception as e:
            all_success = False
            monitor.log_call(
                endpoint=endpoint["name"],
                success=False,
                error_type=str(e)
            )
            print(f"❌ Failed: {str(e)}")
    
    return all_success

def check_quota_info(api_key):
    """Attempt to get quota information."""
    print("\n💰 Quota Information:")
    
    # Get current date in required format
    today = datetime.now().strftime("%Y-%m-%d")
    
    try:
        headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }
        
        response = requests.get(
            f"https://api.openai.com/v1/usage?date={today}",
            headers=headers
        )
        
        if response.status_code == 200:
            data = response.json()
            print("✓ Successfully retrieved quota information")
            print(f"• Today's usage: {data.get('total_usage', 'N/A')} tokens")
            print(f"• Rate limits: {data.get('rate_limits', 'N/A')}")
            
            # Calculate remaining quota
            if 'total_usage' in data and 'hard_limit' in data:
                remaining = data['hard_limit'] - data['total_usage']
                print(f"• Remaining quota: {remaining} tokens")
        else:
            print("⚠️ Could not retrieve quota information")
            print(f"Status code: {response.status_code}")
            print(f"Message: {response.text}")
    except Exception as e:
        print("❌ Error checking quota:")
        print(f"Error: {str(e)}")

def display_usage_stats(monitor):
    """Display API usage statistics."""
    print("\n📊 API Usage Statistics (Last 24h):")
    stats = monitor.get_usage_stats()
    
    if stats:
        print(f"• Total API calls: {stats['total_calls']}")
        print(f"• Successful calls: {stats['successful_calls']}")
        success_rate = (stats['successful_calls'] / stats['total_calls'] * 100) if stats['total_calls'] > 0 else 0
        print(f"• Success rate: {success_rate:.1f}%")
        print(f"• Average response time: {stats['avg_response_time']:.2f}s")
        print(f"• Total tokens used: {stats['total_tokens']}")

def test_openai_integration():
    print("\n🚀 Starting OpenAI API Integration Test")
    
    # Initialize monitoring and rate limiting
    monitor = APIUsageMonitor()
    rate_limiter = RateLimiter()
    
    # Set API key directly for testing
    api_key = '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'
    
    if not verify_api_key(api_key):
        return False
    
    try:
        client = OpenAI(api_key=api_key)
        print("✓ OpenAI client initialized")
        
        # Check API endpoints
        if not check_api_endpoints(client, monitor):
            print("⚠️ Some API endpoints are not accessible")
        
        # Check model compatibility
        compatible_models = check_model_compatibility(client)
        if not compatible_models:
            print("⚠️ No compatible models found")
        
        # Check quota information
        check_quota_info(api_key)
        
        # Try minimal API call with rate limiting
        success = test_minimal_api_call(client, monitor, rate_limiter)
        
        # Display usage statistics
        display_usage_stats(monitor)
        
        return success
    
    except Exception as e:
        print(f"❌ Critical error during setup: {str(e)}")
        return False

if __name__ == '__main__':
    success = test_openai_integration()
    print("\n📋 Final Summary:")
    if success:
        print("✅ Integration test completed successfully!")
    else:
        print("❌ Integration test failed.")
        print("\n💡 Recommended Actions:")
        print("1. Visit platform.openai.com/account/billing to check billing status")
        print("2. Verify API key permissions at platform.openai.com/api-keys")
        print("3. Check usage limits at platform.openai.com/account/rate-limits")
        print("4. Consider implementing rate limiting in your application")
        print("5. Monitor API usage patterns in api_usage.db")
    sys.exit(0 if success else 1)
