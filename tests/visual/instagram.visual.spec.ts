import { test, expect } from '@playwright/test';
import { mockInstagramAPI } from '../mocks/instagram-api';

test.describe('Visual Regression Tests', () => {
  test.beforeEach(async ({ page }) => {
    await mockInstagramAPI(page);
    await page.goto('http://localhost:8080');
  });

  test('homepage matches snapshot', async ({ page }) => {
    await expect(page).toHaveScreenshot('homepage.png', {
      fullPage: true,
      mask: [page.locator('time')] // Mask dynamic content
    });
  });

  test('success state matches snapshot', async ({ page }) => {
    await page.goto('http://localhost:8080?account=THEREAL_MENDES&success=true');
    await expect(page).toHaveScreenshot('success-state.png');
  });

  test('error state matches snapshot', async ({ page }) => {
    await page.goto('http://localhost:8080?account=THEREAL_MENDES&error=Authentication%20failed');
    await expect(page).toHaveScreenshot('error-state.png');
  });

  test('mobile layout matches snapshot', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 }); // iPhone SE
    await expect(page).toHaveScreenshot('mobile-layout.png', {
      fullPage: true
    });
  });

  test('tablet layout matches snapshot', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 }); // iPad
    await expect(page).toHaveScreenshot('tablet-layout.png', {
      fullPage: true
    });
  });

  test('dark mode matches snapshot', async ({ page }) => {
    await page.emulateMedia({ colorScheme: 'dark' });
    await expect(page).toHaveScreenshot('dark-mode.png', {
      fullPage: true
    });
  });

  test('high contrast mode matches snapshot', async ({ page }) => {
    await page.evaluate(() => {
      document.documentElement.setAttribute('data-force-color-contrast', 'high');
    });
    await expect(page).toHaveScreenshot('high-contrast.png', {
      fullPage: true
    });
  });
});
