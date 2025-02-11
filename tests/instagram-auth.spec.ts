import { test, expect } from '@playwright/test';

test('Instagram authentication flow', async ({ page }) => {
  try {
    // Enable verbose logging
    page.on('console', msg => console.log('Browser log:', msg.text()));
    page.on('response', async response => {
      const url = response.url();
      if (url.includes('instagram') || url.includes('facebook')) {
        console.log(`Response from ${url}:`, response.status());
        try {
          const text = await response.text();
          console.log('Response body:', text);
        } catch (e) {
          console.log('Could not get response body');
        }
      }
    });

    // Start the test
    console.log('Starting Instagram auth test...');

    // Navigate to our app
    await page.goto('http://localhost:8080');
    console.log('Navigated to homepage');

    // Wait for the page to load and log the content
    const pageContent = await page.content();
    console.log('Page content:', pageContent);

    // Click the personal account button and log the event
    console.log('Looking for personal account button...');
    const personalButton = await page.getByText('Connect Personal Account');
    console.log('Found button, clicking...');
    await personalButton.click();
    console.log('Clicked personal account button');

    // Wait for navigation and log the URL
    await page.waitForLoadState('networkidle');
    const currentUrl = page.url();
    console.log('Current URL:', currentUrl);

    // Take a screenshot
    await page.screenshot({ path: 'instagram-auth-flow.png', fullPage: true });
    console.log('Screenshot saved');

    // Verify we're on the Instagram auth page
    expect(currentUrl).toContain('api.instagram.com/oauth/authorize');
  } catch (error) {
    console.error('Test error:', error);
    // Take error screenshot
    await page.screenshot({ path: 'instagram-auth-error.png', fullPage: true });
    throw error;
  }
});
