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

interface TikTokService {
    @POST("v2/post/publish/video/init/")
    suspend fun initUpload(
        @Header("Authorization") authorization: String,
        @Query("title") title: String,
        @Query("privacy_level") privacyLevel: Int = 1 // 1 for public
    ): TikTokInitResponse

    @Multipart
    @POST("v2/post/publish/video/fragment/")
    suspend fun uploadChunk(
        @Header("Authorization") authorization: String,
        @Query("upload_id") uploadId: String,
        @Query("chunk_id") chunkId: Int,
        @Part chunk: MultipartBody.Part
    ): TikTokUploadResponse

    @POST("v2/post/publish/status/")
    suspend fun checkStatus(
        @Header("Authorization") authorization: String,
        @Query("upload_id") uploadId: String
    ): TikTokStatusResponse

    @POST("v2/post/publish/")
    suspend fun publishVideo(
        @Header("Authorization") authorization: String,
        @Query("upload_id") uploadId: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("privacy_level") privacyLevel: Int = 1,
        @Query("disable_comment") disableComment: Int = 0,
        @Query("disable_duet") disableDuet: Int = 0
    ): TikTokPublishResponse
}

data class TikTokInitResponse(
    val data: TikTokInitData
)

data class TikTokInitData(
    val upload_id: String,
    val upload_url: String
)

data class TikTokUploadResponse(
    val data: TikTokUploadData
)

data class TikTokUploadData(
    val chunk_id: Int,
    val status: String
)

data class TikTokStatusResponse(
    val data: TikTokStatusData
)

data class TikTokStatusData(
    val status: String,
    val percentage: Int
)

data class TikTokPublishResponse(
    val data: TikTokPublishData
)

data class TikTokPublishData(
    val share_id: String,
    val video_id: String
)

class TikTokClient(private val secureConfig: SecureConfig, private val isCharity: Boolean) {
    companion object {
        private const val CHUNK_SIZE = 5 * 1024 * 1024 // 5MB chunks
    }

    private val service: TikTokService
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
            .baseUrl("https://open.tiktokapis.com/")
            .client(uploadClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(TikTokService::class.java)
    }

    private val appKey: String
        get() = secureConfig.getCredential("TIKTOK_APP_KEY")
            ?: throw IllegalStateException("TikTok app key not found")

    private val appSecret: String
        get() = secureConfig.getCredential("TIKTOK_APP_SECRET")
            ?: throw IllegalStateException("TikTok app secret not found")

    private val accessToken: String
        get() = secureConfig.getCredential(
            if (isCharity) "TIKTOK_CHARITY_ACCESS_TOKEN" else "TIKTOK_PERSONAL_ACCESS_TOKEN"
        ) ?: throw IllegalStateException("TikTok access token not found")

    suspend fun uploadVideo(
        videoFile: File,
        title: String,
        description: String,
        progressCallback: (Float) -> Unit
    ): String {
        try {
            // Step 1: Initialize upload
            val initResponse = service.initUpload(
                authorization = "Bearer $accessToken",
                title = title
            )
            progressCallback(0.1f)

            val uploadId = initResponse.data.upload_id
            
            // Step 2: Upload video chunks
            val totalChunks = (videoFile.length() + CHUNK_SIZE - 1) / CHUNK_SIZE
            videoFile.inputStream().use { input ->
                val buffer = ByteArray(CHUNK_SIZE)
                var chunkId = 0
                var bytesRead: Int
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    val chunk = if (bytesRead == CHUNK_SIZE) buffer else buffer.copyOf(bytesRead)
                    val requestBody = chunk.toRequestBody("video/*".toMediaType())
                    val part = MultipartBody.Part.createFormData("video", "chunk_$chunkId", requestBody)
                    
                    service.uploadChunk(
                        authorization = "Bearer $accessToken",
                        uploadId = uploadId,
                        chunkId = chunkId,
                        chunk = part
                    )
                    
                    chunkId++
                    progressCallback(0.1f + (0.7f * chunkId / totalChunks))
                }
            }

            // Step 3: Check upload status
            var uploadComplete = false
            while (!uploadComplete) {
                val status = service.checkStatus(
                    authorization = "Bearer $accessToken",
                    uploadId = uploadId
                )
                
                if (status.data.status == "success") {
                    uploadComplete = true
                } else if (status.data.status == "failed") {
                    throw IllegalStateException("Upload failed")
                }
                
                progressCallback(0.8f + (0.1f * status.data.percentage / 100))
                kotlinx.coroutines.delay(1000) // Wait 1 second before checking again
            }

            // Step 4: Publish the video
            val publishResponse = service.publishVideo(
                authorization = "Bearer $accessToken",
                uploadId = uploadId,
                title = title,
                description = description
            )
            progressCallback(1.0f)

            return publishResponse.data.video_id
        } catch (e: Exception) {
            throw IllegalStateException("Failed to upload to TikTok: ${e.message}", e)
        }
    }
}
