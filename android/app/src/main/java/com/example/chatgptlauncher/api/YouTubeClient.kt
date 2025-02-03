package com.example.chatgptlauncher.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatgptlauncher.config.SecureConfig
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import net.openid.appauth.*
import java.io.File

class YouTubeClient(
    private val context: Context,
    private val secureConfig: SecureConfig,
    private val isCharity: Boolean
) {
    companion object {
        private const val YOUTUBE_AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val YOUTUBE_TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
        private const val YOUTUBE_SCOPE = "https://www.googleapis.com/auth/youtube.upload"
        private const val APPLICATION_NAME = "Social Media Automation"
    }

    private val clientId: String
        get() = secureConfig.getCredential(
            if (isCharity) "YOUTUBE_CHARITY_CLIENT_ID" else "YOUTUBE_PERSONAL_CLIENT_ID"
        ) ?: throw IllegalStateException("YouTube client ID not found")

    private val clientSecret: String
        get() = secureConfig.getCredential(
            if (isCharity) "YOUTUBE_CHARITY_CLIENT_SECRET" else "YOUTUBE_PERSONAL_CLIENT_SECRET"
        ) ?: throw IllegalStateException("YouTube client secret not found")

    private val transport: HttpTransport = NetHttpTransport()
    private val jsonFactory: JsonFactory = GsonFactory()

    fun getAuthorizationIntent(): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(YOUTUBE_AUTH_ENDPOINT),
            Uri.parse(YOUTUBE_TOKEN_ENDPOINT)
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse("com.example.chatgptlauncher:/oauth2callback")
        )
            .setScope(YOUTUBE_SCOPE)
            .build()

        return AuthorizationService(context).getAuthorizationRequestIntent(authRequest)
    }

    suspend fun uploadVideo(
        videoFile: File,
        title: String,
        description: String,
        accessToken: String,
        progressCallback: (Float) -> Unit
    ): String {
        val youtube = YouTube.Builder(transport, jsonFactory) { request ->
            request.headers["Authorization"] = "Bearer $accessToken"
        }
            .setApplicationName(APPLICATION_NAME)
            .build()

        val video = Video().apply {
            snippet = VideoSnippet().apply {
                this.title = title
                this.description = description
                // Set to Shorts by using vertical video metadata
                this.tags = listOf("#Shorts")
            }
            status = VideoStatus().apply {
                privacyStatus = "public"
                selfDeclaredMadeForKids = false
            }
        }

        val mediaContent = FileContent("video/*", videoFile)
        
        val insert = youtube.videos()
            .insert(listOf("snippet", "status"), video, mediaContent)
            .apply {
                mediaHttpUploader.apply {
                    progressListener = MediaHttpUploader.ProgressListener { uploader ->
                        when (uploader.uploadState) {
                            MediaHttpUploader.UploadState.INITIATION_STARTED -> progressCallback(0.1f)
                            MediaHttpUploader.UploadState.INITIATION_COMPLETE -> progressCallback(0.2f)
                            MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> progressCallback(0.2f + (uploader.progress * 0.6f))
                            MediaHttpUploader.UploadState.MEDIA_COMPLETE -> progressCallback(0.8f)
                            else -> {}
                        }
                    }
                }
            }

        val response = insert.execute()
        progressCallback(1.0f)
        
        return response.id
    }
}
