package com.example.chatgptlauncher.services.upload

import java.io.File
import com.example.chatgptlauncher.services.ContentGenerator.GeneratedContent

interface SocialMediaUploader {
    suspend fun upload(
        videoFile: File,
        content: GeneratedContent,
        isCharityAccount: Boolean,
        progressCallback: (Float) -> Unit
    ): String // Returns the URL of the uploaded content
}
