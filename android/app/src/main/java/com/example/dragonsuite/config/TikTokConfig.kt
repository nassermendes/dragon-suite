package com.example.dragonsuite.config

object TikTokConfig {
    const val CLIENT_KEY = BuildConfig.TIKTOK_CLIENT_KEY
    const val APP_ID = BuildConfig.TIKTOK_APP_ID
    
    // Scopes for TikTok API access
    const val SCOPE_USER_INFO = "user.info.basic"
    const val SCOPE_VIDEO_LIST = "video.list"
    const val SCOPE_VIDEO_UPLOAD = "video.upload"
    const val SCOPE_SHARE = "share.sound.create"
    
    // All required scopes combined
    const val ALL_SCOPES = "$SCOPE_USER_INFO,$SCOPE_VIDEO_LIST,$SCOPE_VIDEO_UPLOAD,$SCOPE_SHARE"
    
    // Deep linking configuration
    const val SCHEME = "tiktok"
    const val HOST = "com.example.dragonsuite"
    const val CALLBACK_URL = "$SCHEME://$HOST/auth"
}
