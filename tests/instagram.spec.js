const { test, expect } = require('@playwright/test');

test.describe('Instagram Integration', () => {
  test('should test Instagram connections', async ({ page }) => {
    // Start the test
    await page.goto('http://localhost:8080/test');
    console.log('Starting Instagram connection test...');

    // Add page error handler
    page.on('console', msg => console.log('Browser log:', msg.text()));
    page.on('pageerror', err => console.error('Browser error:', err));

    // Click test button
    const testButton = page.getByRole('button', { name: 'Test Connections' });
    await expect(testButton).toBeVisible();
    await testButton.click();

    // Verify loading state appears
    const loading = page.locator('#loading');
    await expect(loading).toHaveClass(/active/);

    // Wait for Instagram results
    const results = page.locator('#results');
    await expect(results).toBeVisible();

    // Test THEREAL_MENDES account
    const mendesResult = page.locator('.connection-status', {
      hasText: /Instagram.*THEREAL_MENDES/i
    });
    await expect(mendesResult).toBeVisible({ timeout: 10000 });
    
    // Get and log the full result text
    const mendesText = await mendesResult.textContent();
    console.log('THEREAL_MENDES result:', mendesText);

    // Test ALGARVIOCHARITY account
    const charityResult = page.locator('.connection-status', {
      hasText: /Instagram.*ALGARVIOCHARITY/i
    });
    await expect(charityResult).toBeVisible({ timeout: 10000 });
    
    // Get and log the full result text
    const charityText = await charityResult.textContent();
    console.log('ALGARVIOCHARITY result:', charityText);

    // Verify loading state disappears
    await expect(loading).not.toHaveClass(/active/);

    // Take a screenshot of the results
    await page.screenshot({ path: 'instagram-test-results.png' });

    // Make API call directly to get detailed results
    const response = await page.request.get('http://localhost:8080/api/test-instagram');
    expect(response.ok()).toBeTruthy();
    
    const data = await response.json();
    console.log('Detailed Instagram test results:', data);

    // Verify we have results for both accounts
    expect(data).toHaveProperty('THEREAL_MENDES');
    expect(data).toHaveProperty('ALGARVIOCHARITY');
  });
});
