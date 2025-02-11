import { Router, Request, Response } from 'express';
import { 
  AccountType,
  InstagramSettings,
  MediaContainer,
  MediaPublish,
  ApiResponse,
  UploadStatus,
  VideoUploadRequest,
  VideoUploadResponse
} from '../types/social';
import tokenStorage from '../utils/tokenStorage';
import rateLimiter from '../middleware/rateLimiter';
import logger from '../utils/logger';
import fetch, { Response as FetchResponse } from 'node-fetch';

const instagramRouter = Router();

// Apply rate limiting to all routes
instagramRouter.use(rateLimiter);

// Types for request handlers
interface AuthCallbackQuery {
  code?: string;
  state?: string;
  error?: string;
  error_description?: string;
}

// Helper function to get Instagram auth URL
function getInstagramAuthUrl(account: AccountType): string {
  const redirectUri = process.env.NODE_ENV === 'production' 
    ? 'https://dragon-suite.web.app/auth/instagram/callback'
    : 'http://localhost:8080/auth/instagram/callback';
    
  return `https://api.facebook.com/v18.0/oauth/authorize?` +
    `client_id=${process.env.FACEBOOK_APP_ID}` +
    `&redirect_uri=${encodeURIComponent(redirectUri)}` +
    `&scope=instagram_basic,instagram_content_publish,pages_show_list,pages_read_engagement` +
    `&state=${account}` +
    `&response_type=code`;
}

// Initialize Instagram auth
instagramRouter.get('/auth/init/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    logger.info(`Initializing auth for account: ${account}`);
    
    if (process.env.NODE_ENV === 'test') {
      return res.json({ success: true, message: 'Test mode: Auth initialized' });
    }

    const authUrl = getInstagramAuthUrl(account);
    
    const response: ApiResponse = {
      success: true,
      data: {
        url: authUrl
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to initialize Instagram auth:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: 'Failed to initialize Instagram auth',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Instagram OAuth callback
instagramRouter.get('/auth/callback', async (req: Request, res: Response) => {
  try {
    const query = req.query as AuthCallbackQuery;
    const { code, state, error, error_description } = query;

    if (error || !code || !state) {
      throw new Error(error_description || 'Authorization failed');
    }

    const account = state as AccountType;

    // Exchange code for access token
    const tokenResponse: FetchResponse = await fetch('https://api.facebook.com/v18.0/oauth/access_token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: new URLSearchParams({
        client_id: process.env.FACEBOOK_APP_ID || '',
        client_secret: process.env.FACEBOOK_APP_SECRET || '',
        code: code,
        redirect_uri: process.env.NODE_ENV === 'production'
          ? 'https://dragon-suite.web.app/auth/instagram/callback'
          : 'http://localhost:8080/auth/instagram/callback'
      })
    });

    const tokenData = await tokenResponse.json() as { access_token: string; expires_in: number };
    if (!tokenResponse.ok) {
      throw new Error('Failed to get access token');
    }

    // Get Instagram Business Account ID
    const accountResponse: FetchResponse = await fetch(`https://graph.facebook.com/v18.0/me/accounts?access_token=${tokenData.access_token}`);
    const accountData = await accountResponse.json() as { data: Array<{ id: string; access_token: string }> };
    
    if (!accountResponse.ok || !accountData.data?.length) {
      throw new Error('Failed to get Instagram account');
    }

    const pageToken = accountData.data[0].access_token;
    const pageId = accountData.data[0].id;

    // Get Instagram Business Account ID
    const instagramResponse: FetchResponse = await fetch(
      `https://graph.facebook.com/v18.0/${pageId}?fields=instagram_business_account&access_token=${pageToken}`
    );
    const instagramData = await instagramResponse.json() as { instagram_business_account: { id: string } };

    if (!instagramResponse.ok || !instagramData.instagram_business_account) {
      throw new Error('Failed to get Instagram business account');
    }

    const instagramId = instagramData.instagram_business_account.id;

    // Store tokens
    tokenStorage.setToken('instagram', account, {
      accessToken: pageToken,
      userId: instagramId,
      platform: 'instagram',
      account,
      expiresAt: Date.now() + (tokenData.expires_in * 1000)
    });

    const response: ApiResponse = {
      success: true,
      data: {
        account,
        platform: 'instagram'
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Instagram auth callback failed:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Instagram auth callback failed',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Get settings
instagramRouter.get('/settings/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const token = tokenStorage.getToken('instagram', account);

    const settings: InstagramSettings = {
      accountType: 'BUSINESS',
      mediaTypes: ['IMAGE', 'VIDEO', 'CAROUSEL', 'REELS'],
      permissions: token ? ['instagram_basic', 'instagram_content_publish'] : [],
      uploadStatus: token ? 'ENABLED' : 'DISABLED'
    };

    const response: ApiResponse<InstagramSettings> = {
      success: true,
      data: settings
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to get Instagram settings:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: 'Failed to get Instagram settings',
        type: 'SETTINGS_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Upload media
instagramRouter.post('/media/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const { imageUrl, caption } = req.body;

    const token = tokenStorage.getToken('instagram', account);
    if (!token) {
      throw new Error('Instagram is not connected');
    }

    // Create container
    const containerResponse: FetchResponse = await fetch(
      `https://graph.facebook.com/v18.0/${token.userId}/media?access_token=${token.accessToken}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          image_url: imageUrl,
          caption
        })
      }
    );

    const containerData = await containerResponse.json() as MediaContainer;
    if (!containerResponse.ok) {
      throw new Error('Failed to create media container');
    }

    // Publish media
    const publishResponse: FetchResponse = await fetch(
      `https://graph.facebook.com/v18.0/${token.userId}/media_publish?access_token=${token.accessToken}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          creation_id: containerData.id
        })
      }
    );

    const publishData = await publishResponse.json() as MediaPublish;
    if (!publishResponse.ok) {
      throw new Error('Failed to publish media');
    }

    const response: VideoUploadResponse = {
      success: true,
      mediaId: publishData.id,
      platform: 'instagram',
      account,
      url: publishData.permalink
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to upload media to Instagram:', error);
    const response: VideoUploadResponse = {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to upload media to Instagram',
      platform: 'instagram',
      account: req.params.account
    };
    res.status(500).json(response);
  }
});

// Upload reel
instagramRouter.post('/reel/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const { videoUrl, caption } = req.body as VideoUploadRequest;

    if (!videoUrl) {
      throw new Error('Video URL is required');
    }

    const token = tokenStorage.getToken('instagram', account);
    if (!token) {
      throw new Error('Instagram is not connected');
    }

    // Create container
    const containerResponse: FetchResponse = await fetch(
      `https://graph.facebook.com/v18.0/${token.userId}/media?access_token=${token.accessToken}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          media_type: 'REELS',
          video_url: videoUrl,
          caption,
          share_to_feed: 'true'
        })
      }
    );

    const containerData = await containerResponse.json() as MediaContainer;
    if (!containerResponse.ok) {
      throw new Error('Failed to create media container');
    }

    // Publish media
    const publishResponse: FetchResponse = await fetch(
      `https://graph.facebook.com/v18.0/${token.userId}/media_publish?access_token=${token.accessToken}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          creation_id: containerData.id
        })
      }
    );

    const publishData = await publishResponse.json() as MediaPublish;
    if (!publishResponse.ok) {
      throw new Error('Failed to publish media');
    }

    const response: VideoUploadResponse = {
      success: true,
      mediaId: publishData.id,
      platform: 'instagram',
      account,
      url: publishData.permalink
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to upload reel to Instagram:', error);
    const response: VideoUploadResponse = {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to upload reel to Instagram',
      platform: 'instagram',
      account: req.params.account
    };
    res.status(500).json(response);
  }
});

// Get upload status
instagramRouter.get('/upload-status/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const token = tokenStorage.getToken('instagram', account);

    let status: UploadStatus = 'DISABLED';
    let message = 'Instagram upload is not configured';

    if (token) {
      if (token.expiresAt > Date.now()) {
        status = 'ENABLED';
        message = 'Ready to upload';
      } else {
        status = 'ERROR';
        message = 'Instagram token has expired';
      }
    }

    const response: ApiResponse = {
      success: true,
      data: {
        status,
        message
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to get Instagram upload status:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: 'Failed to get Instagram upload status',
        type: 'STATUS_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

export default instagramRouter;
