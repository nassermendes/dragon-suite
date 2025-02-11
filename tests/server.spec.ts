import { test, expect } from '@playwright/test';

test.describe('Server Health Check', () => {
  test('server should be running and accessible', async ({ page }) => {
    // Try to connect to the server
    const response = await page.goto('http://localhost:8080');
    expect(response?.ok()).toBeTruthy();

    // Wait for any content to load
    await page.waitForLoadState('domcontentloaded');

    // Get the page title or heading
    const pageTitle = await page.title();
    console.log('Page title:', pageTitle);

    // Log the current URL
    console.log('Current URL:', page.url());

    // Log the page content for debugging
    const content = await page.content();
    console.log('Page content:', content.substring(0, 200) + '...');

    // Take a screenshot for debugging
    await page.screenshot({ path: 'test-results/debug-screenshot.png' });
  });
});
