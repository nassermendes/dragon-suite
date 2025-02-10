package com.example.dragonsuite.service

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

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
    val message: String,
    val url: String? = null
)

data class VideoAnalysis(
    val title: String,
    val description: String,
    val hashtags: List<String>
)

class SocialMediaUploadService {
    private val client = OkHttpClient.Builder().build()
    
    private val platformEndpoints = mapOf(
        Platform.INSTAGRAM_REELS to mapOf(
            Account.THEREAL_MENDES to "https://graph.instagram.com/v12.0/me/media",
            Account.ALGARVIOCHARITY to "https://graph.instagram.com/v12.0/me/media"
        ),
        Platform.YOUTUBE_SHORTS to mapOf(
            Account.THEREAL_MENDES to "https://www.googleapis.com/upload/youtube/v3/videos",
            Account.ALGARVIOCHARITY to "https://www.googleapis.com/upload/youtube/v3/videos"
        ),
        Platform.TIKTOK to mapOf(
            Account.THEREAL_MENDES to "https://open-api.tiktok.com/share/video/upload/",
            Account.ALGARVIOCHARITY to "https://open-api.tiktok.com/share/video/upload/"
        )
    )

    private val platformTokens = mapOf(
        Platform.INSTAGRAM_REELS to mapOf(
            Account.THEREAL_MENDES to BuildConfig.INSTAGRAM_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.INSTAGRAM_TOKEN_CHARITY
        ),
        Platform.YOUTUBE_SHORTS to mapOf(
            Account.THEREAL_MENDES to BuildConfig.YOUTUBE_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.YOUTUBE_TOKEN_CHARITY
        ),
        Platform.TIKTOK to mapOf(
            Account.THEREAL_MENDES to BuildConfig.TIKTOK_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.TIKTOK_TOKEN_CHARITY
        )
    )

    suspend fun uploadToAllPlatforms(
        contentResolver: ContentResolver,
        videoUri: Uri,
        videoAnalysis: VideoAnalysis? = null
    ): List<UploadResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<UploadResult>()
        
        // Create a temporary file once for all uploads
        val tempFile = createTempFile(contentResolver, videoUri)
        
        Platform.values().forEach { platform ->
            Account.values().forEach { account ->
                try {
                    val result = uploadToPlatform(
                        platform = platform,
                        account = account,
                        videoFile = tempFile,
                        videoAnalysis = videoAnalysis
                    )
                    results.add(result)
                } catch (e: Exception) {
                    results.add(
                        UploadResult(
                            platform = platform,
                            account = account,
                            success = false,
                            message = "Upload failed: ${e.message}"
                        )
                    )
                }
            }
        }
        
        // Clean up temp file
        tempFile.delete()
        
        results
    }

    private suspend fun uploadToPlatform(
        platform: Platform,
        account: Account,
        videoFile: File,
        videoAnalysis: VideoAnalysis?
    ): UploadResult {
        val endpoint = platformEndpoints[platform]?.get(account)
            ?: throw IllegalStateException("No endpoint found for $platform and $account")
        
        val token = platformTokens[platform]?.get(account)
            ?: throw IllegalStateException("No token found for $platform and $account")

        val requestBody = when (platform) {
            Platform.INSTAGRAM_REELS -> createInstagramRequest(videoFile, videoAnalysis)
            Platform.YOUTUBE_SHORTS -> createYouTubeRequest(videoFile, videoAnalysis)
            Platform.TIKTOK -> createTikTokRequest(videoFile, videoAnalysis)
        }

        val request = Request.Builder()
            .url(endpoint)
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                UploadResult(
                    platform = platform,
                    account = account,
                    success = false,
                    message = "Upload failed: ${response.code}"
                )
            } else {
                val responseBody = response.body?.string()
                UploadResult(
                    platform = platform,
                    account = account,
                    success = true,
                    message = "Upload successful",
                    url = extractUrlFromResponse(platform, responseBody)
                )
            }
        }
    }

    private fun createTempFile(contentResolver: ContentResolver, videoUri: Uri): File {
        val tempFile = File.createTempFile("upload", ".mp4")
        contentResolver.openInputStream(videoUri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun createInstagramRequest(file: File, videoAnalysis: VideoAnalysis?): MultipartBody {
        val caption = buildSocialMediaCaption(videoAnalysis)
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("media_type", "REELS")
            .addFormDataPart("caption", caption)
            .addFormDataPart(
                "video",
                "video.mp4",
                file.asRequestBody("video/mp4".toMediaTypeOrNull())
            )
            .build()
    }

    private fun createYouTubeRequest(file: File, videoAnalysis: VideoAnalysis?): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("part", "snippet,status")
            .addFormDataPart("title", videoAnalysis?.title ?: "New Video")
            .addFormDataPart("description", buildSocialMediaCaption(videoAnalysis))
            .addFormDataPart("privacyStatus", "public")
            .addFormDataPart(
                "video",
                "video.mp4",
                file.asRequestBody("video/mp4".toMediaTypeOrNull())
            )
            .build()
    }

    private fun createTikTokRequest(file: File, videoAnalysis: VideoAnalysis?): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", videoAnalysis?.title ?: "New Video")
            .addFormDataPart("description", buildSocialMediaCaption(videoAnalysis))
            .addFormDataPart(
                "video",
                "video.mp4",
                file.asRequestBody("video/mp4".toMediaTypeOrNull())
            )
            .build()
    }

    private fun buildSocialMediaCaption(videoAnalysis: VideoAnalysis?): String {
        if (videoAnalysis == null) return "Uploaded via Dragon Suite"
        
        return buildString {
            appendLine(videoAnalysis.description)
            appendLine()
            videoAnalysis.hashtags.forEach { hashtag ->
                append("#$hashtag ")
            }
            appendLine()
            appendLine("\nUploaded via Dragon Suite")
        }
    }

    private fun extractUrlFromResponse(platform: Platform, response: String?): String? {
        // Platform-specific response parsing
        return when (platform) {
            Platform.INSTAGRAM_REELS -> extractInstagramUrl(response)
            Platform.YOUTUBE_SHORTS -> extractYouTubeUrl(response)
            Platform.TIKTOK -> extractTikTokUrl(response)
        }
    }

    private fun extractInstagramUrl(response: String?): String? {
        // Parse Instagram API response to get media URL
        return response?.let {
            // Add proper JSON parsing here
            null // Placeholder
        }
    }

    private fun extractYouTubeUrl(response: String?): String? {
        // Parse YouTube API response to get video URL
        return response?.let {
            // Add proper JSON parsing here
            null // Placeholder
        }
    }

    private fun extractTikTokUrl(response: String?): String? {
        // Parse TikTok API response to get video URL
        return response?.let {
            // Add proper JSON parsing here
            null // Placeholder
        }
    }
}
