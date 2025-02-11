import { Router, Request, Response } from 'express';
import { AccountType, TikTokSettings, ApiResponse, UploadStatus, VideoUploadRequest, VideoUploadResponse } from '../types/social';
import tokenStorage from '../utils/tokenStorage';
import logger from '../utils/logger';
import crypto from 'crypto';
import fetch, { Response as FetchResponse } from 'node-fetch';

const tiktokRouter = Router();

const TIKTOK_API_HOST = 'https://open.tiktokapis.com/v2';
const TIKTOK_OAUTH_URL = 'https://www.tiktok.com/v2/auth/authorize/';
const REDIRECT_URI = 'http://localhost:8080/api/tiktok/auth/callback';

const SCOPES = [
  'video.upload',
  'video.list',
  'user.info.basic',
  'video.publish'
] as const;

interface TikTokError {
  code: string;
  message: string;
  log_id?: string;
}

interface TikTokTokenResponse {
  access_token: string;
  expires_in: number;
  open_id: string;
  refresh_token?: string;
  refresh_expires_in?: number;
  scope?: string;
  error?: string;
  error_description?: string;
}

interface TikTokInitResponse {
  video_id: string;
  upload_url?: string;
  error?: TikTokError;
}

interface TikTokPublishResponse {
  video_id: string;
  share_url: string;
  error?: TikTokError;
}

interface TikTokUploadResponse {
  error?: TikTokError;
}

interface TikTokAuthQuery {
  code?: string;
  state?: string;
  error?: string;
  error_description?: string;
}

interface StoredState {
  account: AccountType;
  timestamp: number;
}

// Initialize TikTok auth
tiktokRouter.get('/auth/init/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    logger.info(`Initializing TikTok auth for account: ${account}`);

    // Generate state for CSRF protection
    const state = crypto.randomBytes(32).toString('hex');
    const stateData: StoredState = {
      account,
      timestamp: Date.now()
    };

    // Store state temporarily
    tokenStorage.setState(state, stateData);

    const authUrl = new URL(TIKTOK_OAUTH_URL);
    authUrl.searchParams.append('client_key', process.env.TIKTOK_APP_KEY || '');
    authUrl.searchParams.append('response_type', 'code');
    authUrl.searchParams.append('scope', SCOPES.join(','));
    authUrl.searchParams.append('redirect_uri', REDIRECT_URI);
    authUrl.searchParams.append('state', state);

    const response: ApiResponse = {
      success: true,
      data: {
        url: authUrl.toString()
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to initialize TikTok auth:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Failed to initialize TikTok auth',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// TikTok OAuth callback
tiktokRouter.get('/auth/callback', async (req: Request<unknown, unknown, unknown, TikTokAuthQuery>, res: Response) => {
  try {
    const { code, state, error, error_description } = req.query;

    if (error) {
      throw new Error(error_description || 'Authorization failed');
    }

    if (!code || !state) {
      throw new Error('Missing required parameters');
    }

    // Verify state
    const storedState = tokenStorage.getState<StoredState>(state);
    if (!storedState) {
      throw new Error('Invalid state parameter');
    }

    // Check if state is expired (5 minutes)
    if (Date.now() - storedState.timestamp > 5 * 60 * 1000) {
      throw new Error('State parameter expired');
    }

    // Exchange code for token
    const tokenResponse: FetchResponse = await fetch(`${TIKTOK_API_HOST}/oauth/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Cache-Control': 'no-cache'
      },
      body: new URLSearchParams({
        client_key: process.env.TIKTOK_APP_KEY || '',
        client_secret: process.env.TIKTOK_APP_SECRET || '',
        code,
        grant_type: 'authorization_code',
        redirect_uri: REDIRECT_URI
      })
    });

    const tokenData: TikTokTokenResponse = await tokenResponse.json();
    
    if (!tokenResponse.ok || tokenData.error) {
      throw new Error(tokenData.error_description || 'Failed to get access token');
    }

    // Store tokens with refresh token info
    const expiresAt = Date.now() + (tokenData.expires_in * 1000);
    const refreshExpiresAt = tokenData.refresh_expires_in ? 
      Date.now() + (tokenData.refresh_expires_in * 1000) : 
      undefined;

    tokenStorage.setToken('tiktok', storedState.account, {
      accessToken: tokenData.access_token,
      refreshToken: tokenData.refresh_token,
      userId: tokenData.open_id,
      platform: 'tiktok',
      account: storedState.account,
      expiresAt,
      refreshExpiresAt
    });

    const response: ApiResponse = {
      success: true,
      data: {
        account: storedState.account,
        platform: 'tiktok'
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('TikTok auth callback failed:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'TikTok auth callback failed',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Refresh token
tiktokRouter.post('/refresh/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const token = tokenStorage.getToken('tiktok', account);

    if (!token || !token.refreshToken) {
      throw new Error('No refresh token available');
    }

    // Exchange refresh token for new access token
    const tokenResponse: FetchResponse = await fetch(`${TIKTOK_API_HOST}/oauth/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Cache-Control': 'no-cache'
      },
      body: new URLSearchParams({
        client_key: process.env.TIKTOK_APP_KEY || '',
        client_secret: process.env.TIKTOK_APP_SECRET || '',
        grant_type: 'refresh_token',
        refresh_token: token.refreshToken
      })
    });

    const tokenData: TikTokTokenResponse = await tokenResponse.json();
    
    if (!tokenResponse.ok || tokenData.error) {
      throw new Error(tokenData.error_description || 'Failed to refresh access token');
    }

    // Store new tokens
    const expiresAt = Date.now() + (tokenData.expires_in * 1000);
    const refreshExpiresAt = tokenData.refresh_expires_in ? 
      Date.now() + (tokenData.refresh_expires_in * 1000) : 
      undefined;

    tokenStorage.setToken('tiktok', account, {
      accessToken: tokenData.access_token,
      refreshToken: tokenData.refresh_token,
      userId: tokenData.open_id,
      platform: 'tiktok',
      account,
      expiresAt,
      refreshExpiresAt
    });

    const response: ApiResponse = {
      success: true,
      data: {
        account,
        platform: 'tiktok'
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to refresh TikTok token:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Failed to refresh TikTok token',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Get settings
tiktokRouter.get('/settings/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const token = tokenStorage.getToken('tiktok', account);

    const settings: TikTokSettings = {
      accountType: 'PERSONAL',
      uploadEnabled: !!token,
      permissions: token ? SCOPES : [],
      maxVideoDurationMinutes: 10,
      maxVideoSizeGb: 2,
      allowedFormats: ['mp4'],
      defaultCategory: 'Entertainment'
    };

    const response: ApiResponse<TikTokSettings> = {
      success: true,
      data: settings
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to get TikTok settings:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Failed to get TikTok settings',
        type: 'SETTINGS_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Upload video
tiktokRouter.post('/upload/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const { videoUrl, caption } = req.body as VideoUploadRequest;

    if (!videoUrl) {
      throw new Error('Video URL is required');
    }

    const token = tokenStorage.getToken('tiktok', account);
    if (!token) {
      throw new Error('TikTok is not connected');
    }

    // Download video
    const videoResponse: FetchResponse = await fetch(videoUrl);
    if (!videoResponse.ok) {
      throw new Error('Failed to download video');
    }

    const videoBuffer = await videoResponse.buffer();

    // Initialize upload
    const initResponse: FetchResponse = await fetch(`${TIKTOK_API_HOST}/video/init`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token.accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        post_info: {
          title: caption,
          privacy_level: 'PUBLIC',
          disable_duet: false,
          disable_comment: false,
          disable_stitch: false
        }
      })
    });

    const initData: TikTokInitResponse = await initResponse.json();
    if (!initResponse.ok || initData.error) {
      throw new Error(initData.error?.message || 'Failed to initialize upload');
    }

    // Upload video
    const uploadResponse: FetchResponse = await fetch(initData.upload_url!, {
      method: 'POST',
      headers: {
        'Content-Type': 'video/mp4'
      },
      body: videoBuffer
    });

    const uploadData: TikTokUploadResponse = await uploadResponse.json();
    if (!uploadResponse.ok || uploadData.error) {
      throw new Error(uploadData.error?.message || 'Failed to upload video');
    }

    // Publish video
    const publishResponse: FetchResponse = await fetch(`${TIKTOK_API_HOST}/video/publish`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token.accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        video_id: initData.video_id
      })
    });

    const publishData: TikTokPublishResponse = await publishResponse.json();
    if (!publishResponse.ok || publishData.error) {
      throw new Error(publishData.error?.message || 'Failed to publish video');
    }

    const response: VideoUploadResponse = {
      success: true,
      mediaId: publishData.video_id,
      platform: 'tiktok',
      account,
      url: publishData.share_url
    };

    logger.info(`Successfully uploaded TikTok video for account: ${account}`);
    res.json(response);
  } catch (error) {
    logger.error('Failed to upload video to TikTok:', error);
    const errorResponse: VideoUploadResponse = {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to upload video to TikTok',
      platform: 'tiktok',
      account
    };
    res.status(500).json(errorResponse);
  }
});

export default tiktokRouter;
