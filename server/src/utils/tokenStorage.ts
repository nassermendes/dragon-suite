import { AccountType, Platform, SocialMediaToken } from '../types/social';
import logger from './logger';

interface TokenData {
  accessToken: string;
  refreshToken?: string;
  userId: string;
  expiresAt: number;
  refreshExpiresAt?: number;
}

class TokenStorage {
  private static instance: TokenStorage;
  private tokens: Map<string, SocialMediaToken>;
  private states: Map<string, any>;

  private constructor() {
    this.tokens = new Map();
    this.states = new Map();
    this.initializeFromEnv();
  }

  public static getInstance(): TokenStorage {
    if (!TokenStorage.instance) {
      TokenStorage.instance = new TokenStorage();
    }
    return TokenStorage.instance;
  }

  private getTokenKey(platform: Platform, account: AccountType): string {
    return `${platform}:${account}`;
  }

  private initializeFromEnv(): void {
    try {
      // Initialize Instagram tokens
      const instagramPersonalToken: SocialMediaToken = {
        accessToken: process.env.INSTAGRAM_PERSONAL_ACCESS_TOKEN || '',
        userId: process.env.INSTAGRAM_PERSONAL_USER_ID || '',
        platform: 'instagram',
        account: 'THEREAL_MENDES',
        expiresAt: Date.now() + 60 * 24 * 60 * 60 * 1000 // 60 days
      };
      
      const instagramCharityToken: SocialMediaToken = {
        accessToken: process.env.INSTAGRAM_CHARITY_ACCESS_TOKEN || '',
        userId: process.env.INSTAGRAM_CHARITY_USER_ID || '',
        platform: 'instagram',
        account: 'ALGARVIOCHARITY',
        expiresAt: Date.now() + 60 * 24 * 60 * 60 * 1000 // 60 days
      };

      // Initialize YouTube tokens
      const youtubePersonalToken: SocialMediaToken = {
        accessToken: '',  // Will be obtained through OAuth
        refreshToken: process.env.YOUTUBE_PERSONAL_REFRESH_TOKEN || '',
        userId: process.env.YOUTUBE_PERSONAL_USER_ID || '',
        platform: 'youtube',
        account: 'THEREAL_MENDES',
        expiresAt: 0
      };

      const youtubeCharityToken: SocialMediaToken = {
        accessToken: '',  // Will be obtained through OAuth
        refreshToken: process.env.YOUTUBE_CHARITY_REFRESH_TOKEN || '',
        userId: process.env.YOUTUBE_CHARITY_USER_ID || '',
        platform: 'youtube',
        account: 'ALGARVIOCHARITY',
        expiresAt: 0
      };

      // Initialize TikTok tokens
      const tiktokPersonalToken: SocialMediaToken = {
        accessToken: process.env.TIKTOK_PERSONAL_ACCESS_TOKEN || '',
        refreshToken: process.env.TIKTOK_PERSONAL_REFRESH_TOKEN || '',
        userId: process.env.TIKTOK_PERSONAL_USER_ID || '',
        platform: 'tiktok',
        account: 'THEREAL_MENDES',
        expiresAt: process.env.TIKTOK_PERSONAL_EXPIRES_AT ? parseInt(process.env.TIKTOK_PERSONAL_EXPIRES_AT) : 0,
        refreshExpiresAt: process.env.TIKTOK_PERSONAL_REFRESH_EXPIRES_AT ? parseInt(process.env.TIKTOK_PERSONAL_REFRESH_EXPIRES_AT) : 0
      };

      const tiktokCharityToken: SocialMediaToken = {
        accessToken: process.env.TIKTOK_CHARITY_ACCESS_TOKEN || '',
        refreshToken: process.env.TIKTOK_CHARITY_REFRESH_TOKEN || '',
        userId: process.env.TIKTOK_CHARITY_USER_ID || '',
        platform: 'tiktok',
        account: 'ALGARVIOCHARITY',
        expiresAt: process.env.TIKTOK_CHARITY_EXPIRES_AT ? parseInt(process.env.TIKTOK_CHARITY_EXPIRES_AT) : 0,
        refreshExpiresAt: process.env.TIKTOK_CHARITY_REFRESH_EXPIRES_AT ? parseInt(process.env.TIKTOK_CHARITY_REFRESH_EXPIRES_AT) : 0
      };

      // Store tokens
      this.setToken('instagram', 'THEREAL_MENDES', instagramPersonalToken);
      this.setToken('instagram', 'ALGARVIOCHARITY', instagramCharityToken);
      this.setToken('youtube', 'THEREAL_MENDES', youtubePersonalToken);
      this.setToken('youtube', 'ALGARVIOCHARITY', youtubeCharityToken);
      this.setToken('tiktok', 'THEREAL_MENDES', tiktokPersonalToken);
      this.setToken('tiktok', 'ALGARVIOCHARITY', tiktokCharityToken);
    } catch (error) {
      logger.error('Failed to initialize tokens from env:', error);
    }
  }

  public getToken(platform: Platform, account: AccountType): SocialMediaToken | undefined {
    const key = this.getTokenKey(platform, account);
    const token = this.tokens.get(key);

    if (token && token.expiresAt < Date.now()) {
      logger.warn(`Token for ${platform}:${account} has expired`);
    }

    return token;
  }

  public setToken(platform: Platform, account: AccountType, token: SocialMediaToken): void {
    const key = this.getTokenKey(platform, account);
    this.tokens.set(key, token);
  }

  public setState<T>(state: string, data: T): void {
    this.states.set(state, data);
  }

  public getState<T>(state: string): T | undefined {
    return this.states.get(state) as T | undefined;
  }

  public deleteState(state: string): void {
    this.states.delete(state);
  }
}

export default TokenStorage.getInstance();
