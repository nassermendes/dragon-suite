package com.example.dragonsuite

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.example.dragonsuite.config.OpenAIConfig
import com.example.dragonsuite.service.VideoUploadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.Result

class DragonService : Service() {
    companion object {
        const val ACTION_UPLOAD_VIDEO = "com.example.dragonsuite.UPLOAD_VIDEO"
        const val EXTRA_VIDEO_URI = "video_uri"
        const val EXTRA_UPLOAD_URL = "upload_url"
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 1000L
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val videoUploadService = VideoUploadService()

    override fun onCreate() {
        super.onCreate()
        try {
            OpenAIConfig.initialize(BuildConfig.OPENAI_API_KEY)
        } catch (e: Exception) {
            Log.e("DragonService", "Failed to initialize OpenAI: ${e.message}")
            showErrorNotification("OpenAI initialization failed", e.message ?: "Unknown error")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPLOAD_VIDEO -> {
                val videoUri = intent.getParcelableExtra(EXTRA_VIDEO_URI, Uri::class.java)
                val uploadUrl = intent.getStringExtra(EXTRA_UPLOAD_URL)
                handleVideoUpload(videoUri, uploadUrl)
            }
        }
        return START_NOT_STICKY
    }

    private fun handleVideoUpload(videoUri: Uri?, uploadUrl: String?) {
        if (videoUri == null || uploadUrl == null) {
            Log.e("DragonService", "Invalid video URI or upload URL")
            return
        }

        serviceScope.launch {
            try {
                var retryCount = 0
                var lastError: Throwable? = null

                while (retryCount < MAX_RETRIES) {
                    try {
                        val result = videoUploadService.uploadVideo(contentResolver, videoUri, uploadUrl)
                        result.fold(
                            onSuccess = { response ->
                                showSuccessNotification("Video uploaded successfully")
                                processChatCompletion("Process this upload response: $response")
                            },
                            onFailure = { exception ->
                                lastError = exception
                                retryCount++
                                
                                if (retryCount < MAX_RETRIES) {
                                    val backoffMs = INITIAL_BACKOFF_MS * (1 shl (retryCount - 1))
                                    delay(backoffMs)
                                }
                            }
                        )
                        break
                    } catch (e: Exception) {
                        lastError = e
                        retryCount++
                        
                        if (retryCount < MAX_RETRIES) {
                            val backoffMs = INITIAL_BACKOFF_MS * (1 shl (retryCount - 1))
                            delay(backoffMs)
                        }
                    }
                }

                if (retryCount == MAX_RETRIES) {
                    Log.e("DragonService", "Failed to upload video after $MAX_RETRIES attempts", lastError)
                    showErrorNotification("Upload failed", "Failed after $MAX_RETRIES attempts")
                }
            } catch (e: Exception) {
                Log.e("DragonService", "Error in video upload process", e)
                showErrorNotification("Upload error", e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun processChatCompletion(prompt: String): Result<String> {
        return try {
            var retryCount = 0
            var lastError: Throwable? = null

            while (retryCount < MAX_RETRIES) {
                try {
                    val openAI = OpenAIConfig.getClient()
                    val response = openAI.chatCompletion(
                        ChatCompletionRequest(
                            model = ModelId("gpt-4o-mini"),
                            messages = listOf(
                                ChatMessage(
                                    role = ChatRole.System,
                                    content = """
                                        You are analyzing a video upload response. Please provide:
                                        1. A concise description of the video content
                                        2. Relevant hashtags that describe the video
                                        3. Any notable elements or themes present
                                        Format the response in a clear, structured way.
                                    """.trimIndent()
                                ),
                                ChatMessage(
                                    role = ChatRole.User,
                                    content = prompt
                                )
                            )
                        )
                    )
                    val content = response.choices.firstOrNull()?.message?.content ?: "No response generated"
                    return Result.success(content)
                } catch (e: Exception) {
                    lastError = e
                    retryCount++
                    
                    if (retryCount < MAX_RETRIES) {
                        val backoffMs = INITIAL_BACKOFF_MS * (1 shl (retryCount - 1))
                        delay(backoffMs)
                    }
                }
            }
            
            Result.failure(lastError ?: Exception("Unknown error"))
        } catch (e: Exception) {
            Log.e("DragonService", "Error in chat completion", e)
            Result.failure(e)
        }
    }

    private fun showErrorNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, "dragon_service_errors")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_error)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
    }

    private fun showSuccessNotification(message: String) {
        val notification = NotificationCompat.Builder(this, "dragon_service_status")
            .setContentTitle("Success")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_success)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
