import { test, expect, Page } from '@playwright/test';
import { mockInstagramAPI } from './mocks/instagram-api';
import { AccountType } from '../server/src/types/social';

test.describe('Instagram Integration Tests', () => {
  test.beforeEach(async ({ page }: { page: Page }) => {
    // Set up API mocks
    await mockInstagramAPI(page);
    
    // Start from the homepage
    await page.goto('http://localhost:8080');
  });

  test('page has correct title and header', async ({ page }: { page: Page }) => {
    await expect(page).toHaveTitle(/Dragon Suite - Instagram Integration/);
    const header = page.locator('.header h1');
    await expect(header).toHaveText('Dragon Suite');
  });

  test('displays both Instagram accounts', async ({ page }: { page: Page }) => {
    const personalAccount = page.locator('h2:has-text("@thereal.mendes")');
    const charityAccount = page.locator('h2:has-text("@algarviocharity")');
    
    await expect(personalAccount).toBeVisible();
    await expect(charityAccount).toBeVisible();
  });

  test('connect buttons are present and functional', async ({ page }: { page: Page }) => {
    const personalButton = page.locator('.account-section', { hasText: '@thereal.mendes' }).locator('.button');
    const charityButton = page.locator('.account-section', { hasText: '@algarviocharity' }).locator('.button');
    
    await expect(personalButton).toBeVisible();
    await expect(charityButton).toBeVisible();
    
    await expect(personalButton).toHaveText('Connect Account');
    await expect(charityButton).toHaveText('Connect Account');
    
    // Check if buttons have correct href
    await expect(personalButton).toHaveAttribute('href', '/api/instagram/auth/init/personal');
    await expect(charityButton).toHaveAttribute('href', '/api/instagram/auth/init/charity');
  });

  test('complete authentication flow works', async ({ page }: { page: Page }) => {
    const account: AccountType = 'personal';
    const personalButton = page.locator('.account-section', { hasText: '@thereal.mendes' }).locator('.button');
    
    // Click connect button and wait for redirect
    await Promise.all([
      page.waitForNavigation(),
      personalButton.click()
    ]);

    // Should be redirected back with success
    await expect(page.url()).toContain(`success=true&account=${account}`);
    
    // Check if account status is updated
    const accountStatus = page.locator('.account-status', { hasText: 'Connected' });
    await expect(accountStatus).toBeVisible();
  });

  test('handles authentication errors gracefully', async ({ page }: { page: Page }) => {
    // Mock error response
    await page.route('**/auth/instagram/callback**', async route => {
      await route.fulfill({
        status: 400,
        body: JSON.stringify({
          success: false,
          error: {
            type: 'OAuthException',
            message: 'Invalid authorization code',
            code: 400
          }
        })
      });
    });

    // Try to authenticate
    await page.goto('/api/instagram/auth/init/personal');
    
    // Should show error message
    const errorMessage = page.locator('.error-message');
    await expect(errorMessage).toBeVisible();
    await expect(errorMessage).toContainText('Invalid authorization code');
  });

  test('upload functionality works correctly', async ({ page }: { page: Page }) => {
    const account: AccountType = 'personal';
    const testImagePath = './test-assets/test-image.jpg';
    
    // Mock successful upload
    await page.route('**/media**', async route => {
      await route.fulfill({
        status: 200,
        body: JSON.stringify({
          success: true,
          data: {
            id: 'test_media_id',
            status: 'published'
          }
        })
      });
    });

    // Upload image
    const fileInput = page.locator('input[type="file"]');
    await fileInput.setInputFiles(testImagePath);
    
    // Check upload status
    const uploadStatus = page.locator('.upload-status', { hasText: 'Upload successful' });
    await expect(uploadStatus).toBeVisible();
  });

  test('handles upload errors gracefully', async ({ page }: { page: Page }) => {
    // Mock upload error
    await page.route('**/media**', async route => {
      await route.fulfill({
        status: 400,
        body: JSON.stringify({
          success: false,
          error: {
            type: 'APIError',
            message: 'Invalid file format',
            code: 400
          }
        })
      });
    });

    // Try to upload invalid file
    const fileInput = page.locator('input[type="file"]');
    await fileInput.setInputFiles('./test-assets/invalid.txt');
    
    // Check error message
    const errorMessage = page.locator('.error-message');
    await expect(errorMessage).toBeVisible();
    await expect(errorMessage).toContainText('Invalid file format');
  });
});
