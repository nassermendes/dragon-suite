package com.example.chatgptlauncher.services.upload

import android.content.Context
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.services.ContentGenerator.GeneratedContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import kotlin.time.Duration.Companion.seconds

class TikTokUploader(
    private val context: Context,
    private val secureConfig: SecureConfig
) : SocialMediaUploader {
    companion object {
        private const val API_BASE_URL = "https://open.tiktokapis.com/v2"
        private const val STATUS_CHECK_INTERVAL = 5L // seconds
        private const val MAX_STATUS_CHECKS = 60 // 5 minutes total
    }

    private val client = OkHttpClient.Builder().build()

    override suspend fun upload(
        videoFile: File,
        content: GeneratedContent,
        isCharityAccount: Boolean, // Not used for TikTok as we don't support multiple accounts
        progressCallback: (Float) -> Unit
    ): String = withContext(Dispatchers.IO) {
        progressCallback(0.1f)

        // Step 1: Initialize upload
        val uploadInfo = initializeUpload(videoFile.length())
        progressCallback(0.2f)

        // Step 2: Upload video chunks
        uploadVideoChunks(uploadInfo.uploadUrl, videoFile) { uploadProgress ->
            progressCallback(0.2f + (uploadProgress * 0.4f))
        }

        // Step 3: Submit video
        val publishId = submitVideo(uploadInfo.uploadId, content)
        progressCallback(0.7f)

        // Step 4: Wait for processing
        val videoUrl = waitForProcessing(publishId)
        progressCallback(1.0f)

        return@withContext videoUrl
    }

    private data class UploadInfo(
        val uploadId: String,
        val uploadUrl: String
    )

    private suspend fun initializeUpload(fileSize: Long): UploadInfo {
        val request = Request.Builder()
            .url("$API_BASE_URL/video/upload/")
            .post(FormBody.Builder()
                .add("file_size", fileSize.toString())
                .build())
            .addHeader("Authorization", "Bearer ${secureConfig.getTikTokAccessToken()}")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to initialize TikTok upload: ${response.message}")
        }

        val json = JSONObject(response.body?.string() ?: "{}")
        return UploadInfo(
            uploadId = json.getString("upload_id"),
            uploadUrl = json.getString("upload_url")
        )
    }

    private suspend fun uploadVideoChunks(
        uploadUrl: String,
        videoFile: File,
        progressCallback: (Float) -> Unit
    ) {
        val chunkSize = 5 * 1024 * 1024 // 5MB chunks
        val totalChunks = (videoFile.length() + chunkSize - 1) / chunkSize

        videoFile.inputStream().use { input ->
            var chunkIndex = 0L
            var bytesRead: Int
            val buffer = ByteArray(chunkSize.toInt())

            while (input.read(buffer).also { bytesRead = it } != -1) {
                val chunk = if (bytesRead == buffer.size) buffer else buffer.copyOf(bytesRead)
                
                val requestBody = chunk.toRequestBody("video/*".toMediaType())
                
                val request = Request.Builder()
                    .url(uploadUrl)
                    .put(requestBody)
                    .addHeader("Content-Range", "bytes $chunkIndex-${chunkIndex + bytesRead - 1}/${videoFile.length()}")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception("Failed to upload chunk $chunkIndex: ${response.message}")
                }

                chunkIndex += bytesRead
                progressCallback(chunkIndex.toFloat() / videoFile.length())
            }
        }
    }

    private suspend fun submitVideo(
        uploadId: String,
        content: GeneratedContent
    ): String {
        val caption = buildString {
            appendLine(content.caption)
            appendLine()
            append(content.hashtags.joinToString(" "))
        }

        val request = Request.Builder()
            .url("$API_BASE_URL/video/publish/")
            .post(FormBody.Builder()
                .add("upload_id", uploadId)
                .add("caption", caption)
                .add("privacy_level", "SELF_ONLY") // Start as private
                .build())
            .addHeader("Authorization", "Bearer ${secureConfig.getTikTokAccessToken()}")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to submit TikTok video: ${response.message}")
        }

        val json = JSONObject(response.body?.string() ?: "{}")
        return json.getString("publish_id")
    }

    private suspend fun waitForProcessing(publishId: String): String {
        var attempts = 0
        while (attempts < MAX_STATUS_CHECKS) {
            val request = Request.Builder()
                .url("$API_BASE_URL/video/query/?publish_id=$publishId")
                .get()
                .addHeader("Authorization", "Bearer ${secureConfig.getTikTokAccessToken()}")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("Failed to check video status: ${response.message}")
            }

            val json = JSONObject(response.body?.string() ?: "{}")
            when (json.getString("status")) {
                "PUBLISHED" -> return json.getString("share_url")
                "FAILED" -> throw Exception("TikTok processing failed: ${json.optString("error_message")}")
            }

            delay(STATUS_CHECK_INTERVAL.seconds)
            attempts++
        }

        throw Exception("TikTok processing timed out")
    }
}
