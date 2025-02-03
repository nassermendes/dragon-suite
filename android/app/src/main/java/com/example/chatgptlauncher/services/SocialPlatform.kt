package com.example.chatgptlauncher.services

enum class SocialPlatform {
    YOUTUBE_SHORTS,
    INSTAGRAM_REELS,
    TIKTOK;

    fun getMaxDuration(): Long = when(this) {
        YOUTUBE_SHORTS -> 60_000L // 60 seconds
        INSTAGRAM_REELS -> 90_000L // 90 seconds
        TIKTOK -> 180_000L // 3 minutes
    }

    fun getMaxFileSize(): Long = when(this) {
        YOUTUBE_SHORTS -> 256L * 1024 * 1024 // 256MB
        INSTAGRAM_REELS -> 100L * 1024 * 1024 // 100MB
        TIKTOK -> 287L * 1024 * 1024 // 287MB
    }

    fun getSupportedFormats(): List<String> = when(this) {
        YOUTUBE_SHORTS -> listOf("mp4", "mov")
        INSTAGRAM_REELS -> listOf("mp4")
        TIKTOK -> listOf("mp4")
    }
}
