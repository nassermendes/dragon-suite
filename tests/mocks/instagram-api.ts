import { Page } from '@playwright/test';

export const mockInstagramAPI = async (page: Page) => {
  await page.route('**/v18.0/oauth/authorize*', async (route) => {
    const url = new URL(route.request().url());
    const state = url.searchParams.get('state');
    await route.fulfill({
      status: 302,
      headers: {
        'Location': `http://localhost:8080/auth/instagram/callback?code=mock_auth_code&state=${state}`
      }
    });
  });

  await page.route('**/v18.0/oauth/access_token*', async (route) => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        access_token: 'mock_access_token',
        token_type: 'bearer',
        expires_in: 3600
      })
    });
  });

  await page.route('**/v18.0/me/accounts*', async (route) => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        data: [{
          id: 'mock_page_id',
          access_token: 'mock_page_token',
          name: 'Test Page'
        }]
      })
    });
  });

  await page.route('**/v18.0/**/media*', async (route) => {
    await route.fulfill({
      status: 200,
      body: JSON.stringify({
        id: 'mock_media_id',
        status_code: 'FINISHED'
      })
    });
  });
};
