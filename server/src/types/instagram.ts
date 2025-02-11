export interface InstagramTokens {
  readonly THEREAL_MENDES: {
    readonly accessToken: string;
    readonly userId: string;
  };
  readonly ALGARVIOCHARITY: {
    readonly accessToken: string;
    readonly userId: string;
  };
}

export interface InstagramApiResponse {
  readonly success: boolean;
  readonly data?: {
    readonly id: string;
    readonly username: string;
    readonly media_count?: number;
    readonly account_type?: string;
  };
  readonly error?: {
    readonly type: string;
    readonly message: string;
    readonly code: number;
  };
}

export interface FacebookTokenResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
}

export interface InstagramMediaContainer {
  id: string;
  status_code?: string;
}

export interface InstagramMediaPublish {
  id: string;
}

export interface FacebookApiError {
  error: {
    message: string;
    type: string;
    code: number;
    fbtrace_id: string;
  };
}

export const INSTAGRAM_SCOPES = {
  BASIC: 'instagram_basic',
  CONTENT_PUBLISH: 'instagram_content_publish',
  INSIGHTS: 'instagram_manage_insights',
  PAGES_READ: 'pages_read_engagement',
  PAGES_MANAGE: 'pages_manage_posts',
} as const;
