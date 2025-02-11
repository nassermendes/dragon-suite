import { Page } from '@playwright/test';

export async function mockTikTokAPI(page: Page) {
  // Mock OAuth token endpoint
  await page.route('**/oauth/token*', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        access_token: 'mock_tiktok_access_token',
        open_id: 'mock_tiktok_open_id',
        expires_in: 86400,
        refresh_token: 'mock_tiktok_refresh_token',
        scope: 'video.upload'
      })
    });
  });

  // Mock video init endpoint
  await page.route('**/video/init*', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        upload_url: 'https://mock-upload-url.tiktok.com',
        upload_id: 'mock_upload_id'
      })
    });
  });

  // Mock video upload endpoint
  await page.route('https://mock-upload-url.tiktok.com', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        success: true
      })
    });
  });

  // Mock video publish endpoint
  await page.route('**/video/publish*', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        video_id: 'mock_tiktok_video_id',
        share_url: 'https://www.tiktok.com/@user/video/mock_tiktok_video_id'
      })
    });
  });
}
