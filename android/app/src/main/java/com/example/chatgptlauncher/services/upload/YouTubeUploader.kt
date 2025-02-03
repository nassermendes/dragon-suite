package com.example.chatgptlauncher.services.upload

import android.content.Context
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.services.ContentGenerator.GeneratedContent
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import java.io.File

class YouTubeUploader(
    private val context: Context,
    private val secureConfig: SecureConfig
) : SocialMediaUploader {
    companion object {
        private const val APPLICATION_NAME = "Dragon Suite"
        private const val VIDEO_FILE_FORMAT = "video/*"
    }

    override suspend fun upload(
        videoFile: File,
        content: GeneratedContent,
        isCharityAccount: Boolean,
        progressCallback: (Float) -> Unit
    ): String {
        val credentials = getCredentials(isCharityAccount)
        val youtube = buildYouTubeService(credentials)
        
        progressCallback(0.2f)

        val video = Video().apply {
            snippet = VideoSnippet().apply {
                title = content.title ?: "Video"
                description = buildDescription(content)
                tags = content.hashtags
            }
            status = VideoStatus().apply {
                privacyStatus = "private" // Start as private, can be changed later
                selfDeclaredMadeForKids = false
            }
        }

        progressCallback(0.4f)

        val mediaContent = FileContent(VIDEO_FILE_FORMAT, videoFile)
        
        try {
            val uploadRequest = youtube.videos()
                .insert(listOf("snippet", "status"), video, mediaContent)
                .setNotifySubscribers(false)

            progressCallback(0.6f)
            
            val uploadedVideo = uploadRequest.execute()
            
            progressCallback(1.0f)
            
            return "https://youtu.be/${uploadedVideo.id}"
        } catch (e: GoogleJsonResponseException) {
            throw Exception("Error uploading to YouTube: ${e.details.message}", e)
        }
    }

    private fun buildDescription(content: GeneratedContent): String {
        return buildString {
            appendLine(content.caption)
            appendLine()
            appendLine(content.hashtags.joinToString(" "))
        }
    }

    private fun getCredentials(isCharityAccount: Boolean): GoogleCredential {
        val clientId = if (isCharityAccount) {
            secureConfig.getYouTubeCharityClientId()
        } else {
            secureConfig.getYouTubePersonalClientId()
        }

        val clientSecret = if (isCharityAccount) {
            secureConfig.getYouTubeCharityClientSecret()
        } else {
            secureConfig.getYouTubePersonalClientSecret()
        }

        return GoogleCredential().setClientSecrets(clientId, clientSecret)
    }

    private fun buildYouTubeService(credentials: GoogleCredential): YouTube {
        return YouTube.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credentials
        ).setApplicationName(APPLICATION_NAME).build()
    }
}
