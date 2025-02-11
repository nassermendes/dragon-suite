import { test, expect } from '@playwright/test';
import { mockInstagramAPI } from './mocks/instagram-api';
import { mockYouTubeAPI } from './mocks/youtube-api';
import { mockTikTokAPI } from './mocks/tiktok-api';
import { AccountType, Platform } from '../server/src/types/social';

const ACCOUNTS: AccountType[] = ['THEREAL_MENDES', 'ALGARVIOCHARITY'];
const PLATFORMS: Platform[] = ['instagram', 'youtube', 'tiktok'];

test.describe('Social Media Integration Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Set up API mocks
    await mockInstagramAPI(page);
    await mockYouTubeAPI(page);
    await mockTikTokAPI(page);
    
    await page.goto('http://localhost:8080');
  });

  // Test auth flow for each platform and account combination
  for (const account of ACCOUNTS) {
    for (const platform of PLATFORMS) {
      test(`${platform} auth flow works for ${account}`, async ({ page }) => {
        const connectButton = page.locator(`.account-section[data-account="${account}"][data-platform="${platform}"] .connect-button`);
        
        // Click connect button and wait for redirect
        await Promise.all([
          page.waitForNavigation(),
          connectButton.click()
        ]);

        // Should be redirected back with success
        await expect(page.url()).toContain('success=true');
        await expect(page.url()).toContain(`account=${account}`);
        await expect(page.url()).toContain(`platform=${platform}`);

        // Status message should show success
        const statusDiv = page.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveText('Successfully connected!');
        await expect(statusDiv).toHaveClass(/success/);
      });

      test(`${platform} handles auth errors for ${account}`, async ({ page }) => {
        // Mock error response based on platform
        switch (platform) {
          case 'instagram':
            await page.route('**/v18.0/oauth/access_token*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Invalid auth code' }) }));
            break;
          case 'youtube':
            await page.route('**/oauth2/v4/token*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Invalid auth code' }) }));
            break;
          case 'tiktok':
            await page.route('**/oauth/token*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Invalid auth code' }) }));
            break;
        }

        const connectButton = page.locator(`.account-section[data-account="${account}"][data-platform="${platform}"] .connect-button`);
        
        await Promise.all([
          page.waitForNavigation(),
          connectButton.click()
        ]);

        // Should be redirected back with error
        await expect(page.url()).toContain('error=');
        await expect(page.url()).toContain(`account=${account}`);
        await expect(page.url()).toContain(`platform=${platform}`);

        const statusDiv = page.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveText(/Invalid auth code/);
        await expect(statusDiv).toHaveClass(/error/);
      });

      test(`${platform} video upload works for ${account}`, async ({ page }) => {
        // Mock successful auth
        await page.evaluate(({ account, platform }) => {
          localStorage.setItem(`${platform}_${account}_connected`, 'true');
        }, { account, platform });

        await page.reload();

        // Upload video
        const uploadButton = page.locator(`.account-section[data-account="${account}"][data-platform="${platform}"] .upload-button`);
        const fileInput = page.locator('input[type="file"]');
        
        await fileInput.setInputFiles('./tests/fixtures/test-video.mp4');
        await uploadButton.click();

        // Check success message
        const statusDiv = page.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveText(/Upload successful/);
        await expect(statusDiv).toHaveClass(/success/);
      });

      test(`${platform} handles upload errors for ${account}`, async ({ page }) => {
        // Mock successful auth but failed upload
        await page.evaluate(({ account, platform }) => {
          localStorage.setItem(`${platform}_${account}_connected`, 'true');
        }, { account, platform });

        // Mock upload error based on platform
        switch (platform) {
          case 'instagram':
            await page.route('**/media*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Upload failed' }) }));
            break;
          case 'youtube':
            await page.route('**/youtube/v3/videos*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Upload failed' }) }));
            break;
          case 'tiktok':
            await page.route('**/video/init*', route => 
              route.fulfill({ status: 400, body: JSON.stringify({ error: 'Upload failed' }) }));
            break;
        }

        await page.reload();

        // Try upload
        const uploadButton = page.locator(`.account-section[data-account="${account}"][data-platform="${platform}"] .upload-button`);
        const fileInput = page.locator('input[type="file"]');
        
        await fileInput.setInputFiles('./tests/fixtures/test-video.mp4');
        await uploadButton.click();

        // Check error message
        const statusDiv = page.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveText(/Upload failed/);
        await expect(statusDiv).toHaveClass(/error/);
      });
    }
  }

  test('maintains connection state across platforms', async ({ page, context }) => {
    // Connect all platforms for both accounts
    for (const account of ACCOUNTS) {
      for (const platform of PLATFORMS) {
        await page.evaluate(({ account, platform }) => {
          localStorage.setItem(`${platform}_${account}_connected`, 'true');
        }, { account, platform });
      }
    }

    // Store state
    const cookies = await context.cookies();
    const localStorage = await page.evaluate(() => Object.entries(localStorage));

    // Create new page
    const newPage = await context.newPage();
    await newPage.goto('http://localhost:8080');
    await context.addCookies(cookies);
    await newPage.evaluate(state => {
      state.forEach(([key, value]) => localStorage.setItem(key, value));
    }, localStorage);

    // Verify all connections persist
    for (const account of ACCOUNTS) {
      for (const platform of PLATFORMS) {
        const statusDiv = newPage.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveText('Successfully connected!');
      }
    }
  });

  test('handles network errors gracefully', async ({ page }) => {
    // Simulate offline mode
    await page.route('**/*', route => route.abort('failed'));

    for (const account of ACCOUNTS) {
      for (const platform of PLATFORMS) {
        const connectButton = page.locator(`.account-section[data-account="${account}"][data-platform="${platform}"] .connect-button`);
        await connectButton.click();

        const statusDiv = page.locator(`#${account.toLowerCase()}-${platform}-status`);
        await expect(statusDiv).toHaveClass(/error/);
      }
    }
  });
});
