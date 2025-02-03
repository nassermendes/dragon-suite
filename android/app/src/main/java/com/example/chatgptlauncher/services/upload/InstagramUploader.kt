package com.example.chatgptlauncher.services.upload

import android.content.Context
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.services.ContentGenerator.GeneratedContent
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import kotlin.time.Duration.Companion.seconds

class InstagramUploader(
    private val context: Context,
    private val secureConfig: SecureConfig
) : SocialMediaUploader {
    companion object {
        private const val GRAPH_API_VERSION = "v17.0"
        private const val CONTAINER_STATUS_FINISHED = "FINISHED"
        private const val CONTAINER_STATUS_ERROR = "ERROR"
        private const val STATUS_CHECK_INTERVAL = 5L // seconds
        private const val MAX_STATUS_CHECKS = 60 // 5 minutes total
    }

    override suspend fun upload(
        videoFile: File,
        content: GeneratedContent,
        isCharityAccount: Boolean,
        progressCallback: (Float) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val accessToken = if (isCharityAccount) {
            secureConfig.getInstagramCharityAccessToken()
        } else {
            secureConfig.getInstagramPersonalAccessToken()
        }

        val userId = if (isCharityAccount) {
            secureConfig.getInstagramCharityUserId()
        } else {
            secureConfig.getInstagramPersonalUserId()
        }

        progressCallback(0.1f)

        // Step 1: Create container
        val containerId = createContainer(userId, accessToken)
        progressCallback(0.2f)

        // Step 2: Upload video
        uploadVideo(containerId, videoFile, accessToken)
        progressCallback(0.4f)

        // Step 3: Submit video for publishing
        submitVideo(containerId, content, userId, accessToken)
        progressCallback(0.6f)

        // Step 4: Wait for completion
        val mediaId = waitForCompletion(containerId, accessToken)
        progressCallback(0.8f)

        // Step 5: Get media URL
        val mediaUrl = getMediaUrl(mediaId, accessToken)
        progressCallback(1.0f)

        return@withContext mediaUrl
    }

    private suspend fun createContainer(
        userId: String,
        accessToken: String
    ): String {
        val response = makeGraphRequest(
            endpoint = "/$userId/media",
            parameters = mapOf(
                "media_type" to "REELS",
                "video_url" to "PLACEHOLDER", // Will be updated later
                "access_token" to accessToken
            ),
            method = HttpMethod.POST
        )

        val json = JSONObject(response)
        return json.getString("id")
    }

    private suspend fun uploadVideo(
        containerId: String,
        videoFile: File,
        accessToken: String
    ) {
        makeGraphRequest(
            endpoint = "/$containerId",
            parameters = mapOf(
                "access_token" to accessToken,
                "video_url" to videoFile.path
            ),
            method = HttpMethod.POST
        )
    }

    private suspend fun submitVideo(
        containerId: String,
        content: GeneratedContent,
        userId: String,
        accessToken: String
    ) {
        val caption = buildString {
            appendLine(content.caption)
            appendLine()
            append(content.hashtags.joinToString(" "))
        }

        makeGraphRequest(
            endpoint = "/$userId/media_publish",
            parameters = mapOf(
                "creation_id" to containerId,
                "caption" to caption,
                "access_token" to accessToken
            ),
            method = HttpMethod.POST
        )
    }

    private suspend fun waitForCompletion(
        containerId: String,
        accessToken: String
    ): String {
        var attempts = 0
        while (attempts < MAX_STATUS_CHECKS) {
            val response = makeGraphRequest(
                endpoint = "/$containerId",
                parameters = mapOf("access_token" to accessToken),
                method = HttpMethod.GET
            )

            val json = JSONObject(response)
            when (json.getString("status")) {
                CONTAINER_STATUS_FINISHED -> return json.getString("id")
                CONTAINER_STATUS_ERROR -> throw Exception("Instagram upload failed: ${json.optString("error_message")}")
            }

            delay(STATUS_CHECK_INTERVAL.seconds)
            attempts++
        }

        throw Exception("Instagram upload timed out")
    }

    private suspend fun getMediaUrl(
        mediaId: String,
        accessToken: String
    ): String {
        val response = makeGraphRequest(
            endpoint = "/$mediaId",
            parameters = mapOf(
                "fields" to "permalink",
                "access_token" to accessToken
            ),
            method = HttpMethod.GET
        )

        return JSONObject(response).getString("permalink")
    }

    private suspend fun makeGraphRequest(
        endpoint: String,
        parameters: Map<String, String>,
        method: HttpMethod
    ): String = withContext(Dispatchers.IO) {
        val request = GraphRequest.newGraphPathRequest(
            null,
            "/$GRAPH_API_VERSION$endpoint",
            null
        ).apply {
            httpMethod = method
            parameters.forEach { (key, value) ->
                parameters.putString(key, value)
            }
        }

        val response = request.executeAndWait()
        checkForErrors(response)
        return@withContext response.rawResponse ?: throw Exception("Empty response from Instagram API")
    }

    private fun checkForErrors(response: GraphResponse) {
        val error = response.error
        if (error != null) {
            throw Exception("Instagram API error: ${error.errorMessage}")
        }
    }
}
