// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('Social Media Integration', () => {
  test.beforeEach(async ({ page }) => {
    // Enable console logging
    page.on('console', msg => console.log('Browser log:', msg.text()));

    // Navigate to the test activity and ensure it loads
    await page.goto('/test');
    await expect(page.getByRole('heading', { name: 'Connection Test' })).toBeVisible();
  });

  test('should display connection status for all platforms', async ({ page }) => {
    // Click the test connections button
    const testButton = page.getByRole('button', { name: 'Test Connections' });
    await expect(testButton).toBeVisible();
    await testButton.click();

    // Wait for loading state
    const loading = page.locator('#loading');
    await expect(loading).toHaveClass(/active/);

    // Wait for results to be populated
    const results = page.locator('#results');
    await expect(results).toBeVisible();

    // Wait for all connection statuses to appear
    const statuses = [
      'instagram: connected - Instagram connection successful',
      'youtube: connected - YouTube connection successful',
      'tiktok: connected - TikTok connection successful'
    ];

    for (const status of statuses) {
      await expect(page.locator('.connection-status', {
        hasText: status
      })).toBeVisible({ timeout: 10000 });
    }

    // Verify loading state is gone
    await expect(loading).not.toHaveClass(/active/);
  });

  test('should handle network failures gracefully', async ({ page }) => {
    // Mock failed API response with delay
    await page.route('/api/test-connections', async route => {
      await new Promise(resolve => setTimeout(resolve, 1000));
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ error: 'Failed to connect to services' })
      });
    });
    
    // Click the test connections button
    const testButton = page.getByRole('button', { name: 'Test Connections' });
    await expect(testButton).toBeVisible();
    await testButton.click();

    // Wait for loading state
    const loading = page.locator('#loading');
    await expect(loading).toHaveClass(/active/);

    // Wait for error message
    const errorMessage = page.locator('.error-message');
    await expect(errorMessage).toBeVisible({ timeout: 10000 });
    await expect(errorMessage).toContainText('Unable to test connections');

    // Verify loading state is gone
    await expect(loading).not.toHaveClass(/active/);
  });

  test('should show loading state during connection test', async ({ page }) => {
    // Click the test connections button
    const testButton = page.getByRole('button', { name: 'Test Connections' });
    await expect(testButton).toBeVisible();
    await testButton.click();

    // Wait for loading state
    const loading = page.locator('#loading');
    await expect(loading).toHaveClass(/active/);

    // Wait for loading state to disappear
    await expect(loading).not.toHaveClass(/active/, { timeout: 10000 });
  });

  test('should retry failed connections', async ({ page }) => {
    // Click the test connections button
    await page.getByRole('button', { name: 'Test Connections' }).click();

    // Find and click retry button if present
    const retryButton = page.getByRole('button', { name: 'Retry' });
    if (await retryButton.isVisible()) {
      await retryButton.click();
      
      // Verify retry attempt
      const loadingSpinner = page.getByRole('progressbar');
      await expect(loadingSpinner).toBeVisible();
      await expect(loadingSpinner).not.toBeVisible();
    }
  });
});
