// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('Social Media Integration', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to the test activity
    await page.goto('http://localhost:8080/test');
  });

  test('should display connection status for all platforms', async ({ page }) => {
    // Click the test connections button using role
    await page.getByRole('button', { name: 'Test Connections' }).click();

    // Wait for and verify Instagram connection
    const instagramStatus = page.getByRole('status', { name: /Instagram/ });
    await expect(instagramStatus).toBeVisible();
    await expect(instagramStatus).toHaveText(/Instagram/);

    // Wait for and verify YouTube connection
    const youtubeStatus = page.getByRole('status', { name: /YouTube/ });
    await expect(youtubeStatus).toBeVisible();
    await expect(youtubeStatus).toHaveText(/YouTube/);

    // Wait for and verify TikTok connection
    const tiktokStatus = page.getByRole('status', { name: /TikTok/ });
    await expect(tiktokStatus).toBeVisible();
    await expect(tiktokStatus).toHaveText(/TikTok/);
  });

  test('should handle network failures gracefully', async ({ page }) => {
    // Simulate offline mode
    await page.route('**/*', route => route.abort('failed'));
    
    // Click the test connections button
    await page.getByRole('button', { name: 'Test Connections' }).click();

    // Verify error message is displayed
    const errorAlert = page.getByRole('alert');
    await expect(errorAlert).toBeVisible();
    await expect(errorAlert).toHaveText(/Unable to test connections/);
  });

  test('should show loading state during connection test', async ({ page }) => {
    // Click the test connections button
    await page.getByRole('button', { name: 'Test Connections' }).click();

    // Verify loading state
    const loadingSpinner = page.getByRole('progressbar');
    await expect(loadingSpinner).toBeVisible();

    // Wait for loading to complete
    await expect(loadingSpinner).not.toBeVisible();
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
