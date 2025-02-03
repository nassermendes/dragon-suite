package com.example.chatgptlauncher.api

import com.example.chatgptlauncher.config.SecureConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.util.concurrent.TimeUnit

interface InstagramService {
    @POST("v1/{user_id}/media")
    suspend fun initializeVideoUpload(
        @Path("user_id") userId: String,
        @Query("access_token") accessToken: String,
        @Query("media_type") mediaType: String = "REELS",
        @Query("video_url") videoUrl: String,
        @Query("caption") caption: String,
        @Query("share_to_feed") shareToFeed: Boolean = true
    ): InstagramMediaResponse

    @POST("v1/{user_id}/media_publish")
    suspend fun publishMedia(
        @Path("user_id") userId: String,
        @Query("access_token") accessToken: String,
        @Query("creation_id") creationId: String
    ): InstagramPublishResponse

    @Multipart
    @POST
    suspend fun uploadVideo(
        @Url uploadUrl: String,
        @Part video: MultipartBody.Part
    ): ResponseBody
}

data class InstagramMediaResponse(
    val id: String,
    val video_url: String? = null
)

data class InstagramPublishResponse(
    val id: String
)

class InstagramClient(private val secureConfig: SecureConfig, private val isCharity: Boolean) {
    private val service: InstagramService
    private val uploadClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        uploadClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://graph.instagram.com/")
            .client(uploadClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(InstagramService::class.java)
    }

    private val accessToken: String
        get() = secureConfig.getCredential(
            if (isCharity) "INSTAGRAM_CHARITY_ACCESS_TOKEN" else "INSTAGRAM_PERSONAL_ACCESS_TOKEN"
        ) ?: throw IllegalStateException("Instagram access token not found")

    private val userId: String
        get() = secureConfig.getCredential(
            if (isCharity) "INSTAGRAM_CHARITY_USER_ID" else "INSTAGRAM_PERSONAL_USER_ID"
        ) ?: throw IllegalStateException("Instagram user ID not found")

    suspend fun uploadReel(
        videoFile: File,
        caption: String,
        progressCallback: (Float) -> Unit
    ): String {
        try {
            // Step 1: Upload video to temporary URL
            val uploadUrl = getVideoUploadUrl()
            progressCallback(0.2f)

            val videoRequestBody = videoFile.asRequestBody("video/mp4".toMediaType())
            val videoPart = MultipartBody.Part.createFormData(
                "video",
                videoFile.name,
                videoRequestBody
            )

            service.uploadVideo(uploadUrl, videoPart)
            progressCallback(0.6f)

            // Step 2: Initialize media container
            val mediaResponse = service.initializeVideoUpload(
                userId = userId,
                accessToken = accessToken,
                caption = caption,
                videoUrl = uploadUrl
            )
            progressCallback(0.8f)

            // Step 3: Publish the media
            val publishResponse = service.publishMedia(
                userId = userId,
                accessToken = accessToken,
                creationId = mediaResponse.id
            )
            progressCallback(1.0f)

            return publishResponse.id
        } catch (e: Exception) {
            throw IllegalStateException("Failed to upload to Instagram: ${e.message}", e)
        }
    }

    private suspend fun getVideoUploadUrl(): String {
        // In a real implementation, this would get a temporary upload URL from Instagram
        // For now, we'll use a placeholder
        return "https://upload.instagram.com/temp-video"
    }
}
