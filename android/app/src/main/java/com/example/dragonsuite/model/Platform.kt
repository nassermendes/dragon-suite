package com.example.dragonsuite.model

enum class Platform {
    INSTAGRAM_REELS,
    YOUTUBE_SHORTS,
    TIKTOK
}

enum class Account {
    THEREAL_MENDES,
    ALGARVIOCHARITY
}

data class UploadResult(
    val platform: Platform,
    val account: Account,
    val success: Boolean,
    val message: String? = null,
    val url: String? = null
)

data class ConnectionTestResult(
    val platform: Platform,
    val account: Account,
    val isConnected: Boolean,
    val accountName: String?,
    val error: String?
)
