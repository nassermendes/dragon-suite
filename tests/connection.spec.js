// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('Social Media Integration', () => {
  test.beforeEach(async ({ page }) => {
    // Enable console logging
    page.on('console', msg => console.log('Browser log:', msg.text()));

    // Navigate to the test activity and ensure it loads
    await page.goto('http://localhost:8080/test');
    await expect(page.getByRole('heading', { name: 'Connection Test' })).toBeVisible();
  });

  test('should test all platform connections', async ({ page }) => {
    // Start the test
    await page.goto('http://localhost:8080/test');
    console.log('Starting connection test...');

    // Click test button
    const testButton = page.getByRole('button', { name: 'Test Connections' });
    await expect(testButton).toBeVisible();
    await testButton.click();

    // Verify loading state appears
    const loading = page.locator('#loading');
    await expect(loading).toHaveClass(/active/);

    // Wait for results
    const results = page.locator('#results');
    await expect(results).toBeVisible();

    // Wait for all connection status elements to appear
    await expect(page.locator('.connection-status')).toHaveCount(6, { timeout: 10000 });

    // Verify all platforms have results (either success or error)
    const platforms = ['Instagram', 'YouTube', 'TikTok'];
    const accounts = ['THEREAL_MENDES', 'ALGARVIOCHARITY'];

    for (const platform of platforms) {
      for (const account of accounts) {
        await expect(
          page.locator('.connection-status', {
            hasText: new RegExp(`${platform}.*${account}`, 'i')
          })
        ).toBeVisible({ timeout: 10000 });
      }
    }

    // Verify loading state disappears
    await expect(loading).not.toHaveClass(/active/);

    // Take a screenshot of the results
    await page.screenshot({ path: 'test-results.png' });
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
