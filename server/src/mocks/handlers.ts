import { rest, RestRequest, RestContext } from 'msw';
import { AccountType } from '../types/social';

interface FacebookAuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
}

interface FacebookPageResponse {
  data: Array<{
    id: string;
    access_token: string;
    name: string;
    instagram_business_account?: {
      id: string;
    };
  }>;
}

interface FacebookMediaResponse {
  id: string;
  status_code: string;
}

interface FacebookPublishResponse {
  id: string;
}

interface YouTubeAuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  refresh_token: string;
}

interface YouTubeUploadResponse {
  id: string;
  snippet: {
    title: string;
    description: string;
    categoryId: string;
  };
  status: {
    privacyStatus: string;
    publishAt?: string;
  };
}

interface TikTokAuthResponse {
  access_token: string;
  expires_in: number;
  open_id: string;
  refresh_token: string;
  refresh_expires_in: number;
  scope: string;
}

interface TikTokUploadResponse {
  data: {
    video_id: string;
    share_url: string;
  };
}

export const handlers = [
  // Instagram/Facebook Handlers
  rest.get('https://api.facebook.com/v18.0/oauth/authorize', (req: RestRequest, res, ctx: RestContext) => {
    const state = req.url.searchParams.get('state');
    return res(
      ctx.status(302),
      ctx.set('Location', `http://localhost:8080/api/instagram/auth/callback?code=mock_auth_code&state=${state}`)
    );
  }),

  rest.get('https://api.facebook.com/v18.0/oauth/access_token', (_req: RestRequest, res, ctx: RestContext) => {
    const response: FacebookAuthResponse = {
      access_token: 'mock_access_token',
      token_type: 'bearer',
      expires_in: 3600
    };
    return res(ctx.json(response));
  }),

  rest.get('https://graph.facebook.com/v18.0/me/accounts', (_req: RestRequest, res, ctx: RestContext) => {
    const response: FacebookPageResponse = {
      data: [{
        id: 'mock_page_id',
        access_token: 'mock_page_token',
        name: 'Test Page',
        instagram_business_account: {
          id: 'mock_instagram_id'
        }
      }]
    };
    return res(ctx.json(response));
  }),

  rest.post('https://graph.facebook.com/v18.0/:account_id/media', async (req: RestRequest, res, ctx: RestContext) => {
    const response: FacebookMediaResponse = {
      id: 'mock_media_id',
      status_code: 'FINISHED'
    };
    return res(ctx.json(response));
  }),

  rest.post('https://graph.facebook.com/v18.0/:media_id/media_publish', (_req: RestRequest, res, ctx: RestContext) => {
    const response: FacebookPublishResponse = {
      id: 'mock_post_id'
    };
    return res(ctx.json(response));
  }),

  // YouTube Handlers
  rest.post('https://oauth2.googleapis.com/token', (_req: RestRequest, res, ctx: RestContext) => {
    const response: YouTubeAuthResponse = {
      access_token: 'mock_youtube_token',
      token_type: 'Bearer',
      expires_in: 3600,
      refresh_token: 'mock_refresh_token'
    };
    return res(ctx.json(response));
  }),

  rest.post('https://www.googleapis.com/upload/youtube/v3/videos', async (_req: RestRequest, res, ctx: RestContext) => {
    const response: YouTubeUploadResponse = {
      id: 'mock_youtube_video_id',
      snippet: {
        title: 'Test Video',
        description: 'Test Description',
        categoryId: '22'
      },
      status: {
        privacyStatus: 'public'
      }
    };
    return res(ctx.json(response));
  }),

  // TikTok Handlers
  rest.post('https://open.tiktokapis.com/v2/oauth/token', (_req: RestRequest, res, ctx: RestContext) => {
    const response: TikTokAuthResponse = {
      access_token: 'mock_tiktok_token',
      expires_in: 3600,
      open_id: 'mock_open_id',
      refresh_token: 'mock_refresh_token',
      refresh_expires_in: 86400,
      scope: 'video.upload,video.list,user.info.basic'
    };
    return res(ctx.json(response));
  }),

  rest.post('https://open.tiktokapis.com/v2/video/upload', async (_req: RestRequest, res, ctx: RestContext) => {
    const response: TikTokUploadResponse = {
      data: {
        video_id: 'mock_tiktok_video_id',
        share_url: 'https://www.tiktok.com/@user/video/mock_tiktok_video_id'
      }
    };
    return res(ctx.json(response));
  })
];
