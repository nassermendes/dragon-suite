import { test, expect } from '@playwright/test';
import type { InstagramSettings, YouTubeSettings, TikTokSettings, AccountType } from '../server/src/types/social';

const TEST_ACCOUNTS: AccountType[] = ['THEREAL_MENDES', 'ALGARVIOCHARITY'];
const TEST_PORT = 8081;

test.describe('Platform Settings Tests', () => {
  for (const account of TEST_ACCOUNTS) {
    test(`Instagram settings for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/instagram/settings/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const settings = await response.json() as InstagramSettings;
      expect(settings.accountType).toBe('BUSINESS');
      expect(settings.uploadEnabled).toBe(true);
      expect(settings.mediaTypes).toContain('REELS');
      expect(settings.maxVideoDurationMinutes).toBeGreaterThan(0);
      expect(settings.maxVideoSizeMb).toBeGreaterThan(0);
    });

    test(`YouTube settings for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/youtube/settings/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const settings = await response.json() as YouTubeSettings;
      expect(settings.uploadStatus).toBe('ENABLED');
      expect(settings.privacyStatus).toBeDefined();
      expect(settings.maxVideoDurationMinutes).toBeGreaterThan(0);
      expect(settings.maxVideoSizeGb).toBeGreaterThan(0);
      expect(settings.allowedFormats).toBeDefined();
    });

    test(`TikTok settings for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/tiktok/settings/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const settings = await response.json() as TikTokSettings;
      expect(settings.accountType).toBe('BUSINESS');
      expect(settings.uploadEnabled).toBe(true);
      expect(settings.maxVideoDurationMinutes).toBeGreaterThan(0);
      expect(settings.maxVideoSizeMb).toBeGreaterThan(0);
      expect(settings.allowedFormats).toBeDefined();
    });
  }
});

test.describe('Upload Status Tests', () => {
  for (const account of TEST_ACCOUNTS) {
    test(`Instagram upload status for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/instagram/upload-status/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const status = await response.json();
      expect(status).toHaveProperty('status');
      expect(['pending', 'processing', 'completed', 'failed']).toContain(status.status);
    });

    test(`YouTube upload status for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/youtube/upload-status/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const status = await response.json();
      expect(status).toHaveProperty('status');
      expect(['pending', 'processing', 'completed', 'failed']).toContain(status.status);
    });

    test(`TikTok upload status for ${account}`, async ({ request }) => {
      const response = await request.get(`http://localhost:${TEST_PORT}/api/tiktok/upload-status/${account}`);
      expect(response.ok()).toBeTruthy();
      
      const status = await response.json();
      expect(status).toHaveProperty('status');
      expect(['pending', 'processing', 'completed', 'failed']).toContain(status.status);
    });
  }
});
