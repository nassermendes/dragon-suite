export type AccountType = 'THEREAL_MENDES' | 'ALGARVIOCHARITY';

export type Platform = 'instagram' | 'youtube' | 'tiktok';

export interface SocialMediaToken {
  accessToken: string;
  refreshToken?: string;
  userId: string;
  platform: Platform;
  account: AccountType;
  expiresAt: number;
  refreshExpiresAt?: number;
}

export type UploadStatus = 'ENABLED' | 'DISABLED' | 'PENDING' | 'ERROR';

export interface InstagramSettings {
  accountType: 'PERSONAL' | 'BUSINESS';
  mediaTypes: string[];
  permissions: string[];
  uploadStatus: UploadStatus;
}

export interface YouTubeSettings {
  uploadStatus: UploadStatus;
  privacyStatus: 'private' | 'unlisted' | 'public';
  permissions: string[];
  maxVideoDurationMinutes: number;
  maxVideoSizeGb: number;
  allowedFormats: string[];
  defaultCategory: string;
}

export interface TikTokSettings {
  accountType: 'PERSONAL' | 'BUSINESS';
  uploadEnabled: boolean;
  permissions: string[];
  maxVideoDurationMinutes: number;
  maxVideoSizeMb: number;
  allowedFormats: string[];
  defaultPrivacy: 'public' | 'private' | 'friends';
  allowComments: boolean;
  allowDuets: boolean;
  allowStitch: boolean;
}

export interface MediaContainer {
  id: string;
  status: string;
  statusCode: number;
}

export interface MediaPublish {
  id: string;
  status: string;
  statusCode: number;
  permalink?: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: {
    message: string;
    type: string;
    code: number;
  };
}

export interface VideoUploadRequest {
  videoUrl?: string;
  caption: string;
  account: AccountType;
  platform: Platform;
  metadata?: Record<string, any>;
}

export interface VideoUploadResponse {
  success: boolean;
  mediaId?: string;
  error?: string;
  platform: Platform;
  account: AccountType;
  url?: string;
}

export interface AuthStatus {
  isConnected: boolean;
  platform: Platform;
  account: AccountType;
  error?: string;
}

export interface TokenStorage {
  [key: string]: SocialMediaToken;
}
