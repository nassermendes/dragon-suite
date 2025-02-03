package com.example.chatgptlauncher.model

enum class SocialPlatform(
    val displayName: String,
    val maxVideoDuration: Int, // in seconds
    val maxFileSize: Long // in bytes
) {
    YOUTUBE(
        displayName = "YouTube",
        maxVideoDuration = 43200, // 12 hours
        maxFileSize = 128_000_000_000 // 128 GB
    ),
    INSTAGRAM(
        displayName = "Instagram",
        maxVideoDuration = 900, // 15 minutes
        maxFileSize = 3_600_000_000 // 3.6 GB
    ),
    TIKTOK(
        displayName = "TikTok",
        maxVideoDuration = 600, // 10 minutes
        maxFileSize = 2_000_000_000 // 2 GB
    );

    companion object {
        fun fromString(name: String): SocialPlatform {
            return values().find { it.name.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown platform: $name")
        }
    }
}
