import fetch, { Response as FetchResponse } from 'node-fetch';
import { AccountType, ApiResponse, InstagramProfileData, Platform } from './types/social';

interface TestResult {
  success: boolean;
  error?: string;
  data?: unknown;
}

interface ConnectionTest {
  name: string;
  endpoint: string;
  method: 'GET' | 'POST';
  body?: unknown;
}

async function testConnection(platform: Platform, account: AccountType): Promise<TestResult[]> {
  const baseUrl = `http://localhost:8080/api/${platform}`;
  const results: TestResult[] = [];

  const tests: ConnectionTest[] = [
    {
      name: 'Profile',
      endpoint: `/profile/${account}`,
      method: 'GET'
    },
    {
      name: 'Settings',
      endpoint: `/settings/${account}`,
      method: 'GET'
    }
  ];

  for (const test of tests) {
    try {
      const response: FetchResponse = await fetch(`${baseUrl}${test.endpoint}`, {
        method: test.method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: test.body ? JSON.stringify(test.body) : undefined
      });

      const data: ApiResponse<unknown> = await response.json();
      results.push({
        success: data.success,
        error: data.error?.message,
        data: data.data
      });
    } catch (error) {
      results.push({
        success: false,
        error: error instanceof Error ? error.message : 'Unknown error'
      });
    }
  }

  return results;
}

async function runTests(): Promise<void> {
  const platforms: Platform[] = ['instagram', 'youtube', 'tiktok'];
  const accounts: AccountType[] = ['personal', 'charity'];

  for (const platform of platforms) {
    console.log(`\nTesting ${platform} connections...`);

    for (const account of accounts) {
      console.log(`\n${platform} - ${account}:`);
      const results = await testConnection(platform, account);

      results.forEach((result, index) => {
        const testName = `Test ${index + 1}`;
        if (result.success) {
          console.log(`✅ ${testName} successful`);
          if (result.data) {
            console.log('Data:', result.data);
          }
        } else {
          console.log(`❌ ${testName} failed`);
          if (result.error) {
            console.log('Error:', result.error);
          }
        }
      });
    }
  }
}

// Only run if this file is being executed directly
if (require.main === module) {
  runTests().catch(error => {
    console.error('Test execution failed:', error instanceof Error ? error.message : 'Unknown error');
    process.exit(1);
  });
}

export { testConnection, runTests };
