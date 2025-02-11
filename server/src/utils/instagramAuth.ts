import fetch, { Response as FetchResponse } from 'node-fetch';
import { InstagramApiResponse, InstagramError, InstagramUserData, InstagramMediaData } from '../types/instagram';

const INSTAGRAM_API_VERSION = 'v18.0';
const BASE_URL = `https://graph.instagram.com/${INSTAGRAM_API_VERSION}`;

interface InstagramErrorResponse {
  error?: {
    type: string;
    message: string;
    code?: number;
  };
}

interface InstagramUserResponse {
  id: string;
  username: string;
  account_type: string;
}

interface InstagramMediaResponse {
  data: Array<{
    id: string;
    caption?: string;
    media_type: string;
    media_url: string;
    permalink: string;
  }>;
  paging?: {
    cursors: {
      before: string;
      after: string;
    };
    next: string;
  };
}

export async function verifyInstagramToken(token: string): Promise<InstagramApiResponse<InstagramUserData>> {
  try {
    const response: FetchResponse = await fetch(`${BASE_URL}/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    const data: InstagramUserResponse & InstagramErrorResponse = await response.json();
    
    if (!response.ok) {
      const error: InstagramError = {
        type: data.error?.type || 'UnknownError',
        message: data.error?.message || 'Failed to verify token',
        code: response.status
      };
      return {
        success: false,
        error
      };
    }
    
    return {
      success: true,
      data: {
        id: data.id,
        username: data.username,
        account_type: data.account_type
      }
    };
  } catch (error) {
    const apiError: InstagramError = {
      type: 'NetworkError',
      message: error instanceof Error ? error.message : 'Network request failed',
      code: 500
    };
    return {
      success: false,
      error: apiError
    };
  }
}

export async function getInstagramUserMedia(token: string): Promise<InstagramApiResponse<InstagramMediaData>> {
  try {
    const response: FetchResponse = await fetch(
      `${BASE_URL}/me/media?fields=id,caption,media_type,media_url,permalink`, {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );

    const data: InstagramMediaResponse & InstagramErrorResponse = await response.json();

    if (!response.ok) {
      const error: InstagramError = {
        type: data.error?.type || 'UnknownError',
        message: data.error?.message || 'Failed to fetch user media',
        code: response.status
      };
      return {
        success: false,
        error
      };
    }

    return {
      success: true,
      data: {
        items: data.data.map(item => ({
          id: item.id,
          caption: item.caption || '',
          media_type: item.media_type,
          media_url: item.media_url,
          permalink: item.permalink
        })),
        paging: data.paging
      }
    };
  } catch (error) {
    const apiError: InstagramError = {
      type: 'NetworkError',
      message: error instanceof Error ? error.message : 'Network request failed',
      code: 500
    };
    return {
      success: false,
      error: apiError
    };
  }
}
