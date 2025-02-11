import { Router, Request, Response } from 'express';
import { google } from 'googleapis';
import logger from '../utils/logger';
import { AccountType, VideoUploadRequest, VideoUploadResponse, YouTubeSettings, UploadStatus, ApiResponse } from '../types/social';
import tokenStorage from '../utils/tokenStorage';
import fetch, { Response as FetchResponse } from 'node-fetch';

const youtubeRouter = Router();
const youtube = google.youtube('v3');

// Initialize YouTube auth
youtubeRouter.get('/auth/init/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    logger.info(`Initializing YouTube auth for account: ${account}`);

    const oauth2Client = new google.auth.OAuth2(
      process.env.YOUTUBE_CLIENT_ID,
      process.env.YOUTUBE_CLIENT_SECRET,
      'http://localhost:8080/auth/youtube/callback'
    );

    const scopes = [
      'https://www.googleapis.com/auth/youtube.upload',
      'https://www.googleapis.com/auth/youtube'
    ];

    const authUrl = oauth2Client.generateAuthUrl({
      access_type: 'offline',
      scope: scopes,
      state: account
    });

    const response: ApiResponse = {
      success: true,
      data: {
        url: authUrl
      }
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to initialize YouTube auth:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Failed to initialize YouTube auth',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// YouTube OAuth callback
youtubeRouter.get('/auth/callback', async (req: Request, res: Response) => {
  try {
    const { code, state, error } = req.query;
    
    if (error || !code || !state) {
      throw new Error('Authorization failed');
    }

    const account = state as AccountType;
    const oauth2Client = new google.auth.OAuth2(
      process.env.YOUTUBE_CLIENT_ID,
      process.env.YOUTUBE_CLIENT_SECRET,
      'http://localhost:8080/auth/youtube/callback'
    );

    const { tokens } = await oauth2Client.getToken(code as string);
    oauth2Client.setCredentials(tokens);

    // Get channel info
    const response = await youtube.channels.list({
      auth: oauth2Client,
      part: ['id', 'snippet'],
      mine: true
    });

    if (!response.data.items?.[0]) {
      throw new Error('Failed to get channel info');
    }

    const channelId = response.data.items[0].id;
    if (!channelId) {
      throw new Error('Channel ID not found');
    }

    // Store tokens
    tokenStorage.setToken('youtube', account, {
      accessToken: tokens.access_token!,
      userId: channelId,
      platform: 'youtube',
      account,
      expiresAt: tokens.expiry_date || Date.now() + 3600000
    });

    const apiResponse: ApiResponse = {
      success: true,
      data: {
        account,
        platform: 'youtube'
      }
    };

    res.json(apiResponse);
  } catch (error) {
    logger.error('YouTube auth callback failed:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'YouTube auth callback failed',
        type: 'AUTH_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Get settings
youtubeRouter.get('/settings/:account', (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const token = tokenStorage.getToken('youtube', account);

    const settings: YouTubeSettings = {
      uploadStatus: token ? 'ENABLED' : 'DISABLED',
      privacyStatus: 'public',
      permissions: token ? ['youtube.upload', 'youtube'] : [],
      maxVideoDurationMinutes: 60,
      maxVideoSizeGb: 128,
      allowedFormats: ['mp4', 'mov'],
      defaultCategory: 'Entertainment'
    };

    const response: ApiResponse<YouTubeSettings> = {
      success: true,
      data: settings
    };

    res.json(response);
  } catch (error) {
    logger.error('Failed to get YouTube settings:', error);
    const response: ApiResponse = {
      success: false,
      error: {
        message: error instanceof Error ? error.message : 'Failed to get YouTube settings',
        type: 'SETTINGS_ERROR',
        code: 500
      }
    };
    res.status(500).json(response);
  }
});

// Upload Short
youtubeRouter.post('/shorts/:account', async (req: Request<{ account: AccountType }>, res: Response) => {
  try {
    const { account } = req.params;
    const { videoUrl, caption } = req.body as VideoUploadRequest;

    if (!videoUrl) {
      throw new Error('Video URL is required');
    }

    const token = tokenStorage.getToken('youtube', account);
    if (!token) {
      throw new Error('YouTube is not connected');
    }

    // Download video
    const videoResponse: FetchResponse = await fetch(videoUrl);
    if (!videoResponse.ok) {
      throw new Error('Failed to download video');
    }

    const videoBuffer = await videoResponse.buffer();

    // Upload to YouTube
    const oauth2Client = new google.auth.OAuth2();
    oauth2Client.setCredentials({ access_token: token.accessToken });

    const response = await youtube.videos.insert({
      auth: oauth2Client,
      part: ['snippet', 'status'],
      requestBody: {
        snippet: {
          title: caption,
          description: caption,
          categoryId: '22' // People & Blogs
        },
        status: {
          privacyStatus: 'public',
          selfDeclaredMadeForKids: false
        }
      },
      media: {
        body: videoBuffer
      }
    });

    if (!response.data.id) {
      throw new Error('Failed to upload video');
    }

    const uploadResponse: VideoUploadResponse = {
      success: true,
      mediaId: response.data.id,
      platform: 'youtube',
      account,
      url: `https://www.youtube.com/watch?v=${response.data.id}`
    };

    logger.info(`Successfully uploaded YouTube Short for account: ${account}`);
    res.json(uploadResponse);
  } catch (error) {
    logger.error('Failed to upload video to YouTube:', error);
    const errorResponse: VideoUploadResponse = {
      success: false,
      error: error instanceof Error ? error.message : 'Failed to upload video to YouTube',
      platform: 'youtube',
      account
    };
    
    res.status(500).json(errorResponse);
  }
});

export default youtubeRouter;
