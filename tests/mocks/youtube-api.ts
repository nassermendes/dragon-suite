import { Page } from '@playwright/test';

export async function mockYouTubeAPI(page: Page) {
  // Mock OAuth token endpoint
  await page.route('**/oauth2/v4/token*', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        access_token: 'mock_youtube_access_token',
        refresh_token: 'mock_youtube_refresh_token',
        expires_in: 3600,
        token_type: 'Bearer'
      })
    });
  });

  // Mock video upload endpoint
  await page.route('**/youtube/v3/videos*', async route => {
    const method = route.request().method();
    if (method === 'POST') {
      await route.fulfill({
        status: 200,
        body: JSON.stringify({
          id: 'mock_youtube_video_id',
          status: {
            uploadStatus: 'processed',
            privacyStatus: 'public'
          }
        })
      });
    }
  });

  // Mock video status endpoint
  await page.route('**/youtube/v3/videos/status*', async route => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        id: 'mock_youtube_video_id',
        status: {
          uploadStatus: 'processed',
          privacyStatus: 'public'
        }
      })
    });
  });
}
