package com.example.dragonsuite

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
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
import kotlinx.coroutines.launch

class DragonService : Service() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "DragonSuite"
        const val NOTIFICATION_ID = 1
        const val ACTION_UPLOAD_VIDEO = "com.example.dragonsuite.UPLOAD_VIDEO"
        const val EXTRA_VIDEO_URI = "video_uri"
        const val EXTRA_UPLOAD_URL = "upload_url"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val videoUploadService = VideoUploadService()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground()
        
        // Initialize OpenAI with your API key
        // TODO: Store this securely and inject it properly
        OpenAIConfig.initialize("your-api-key-here")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPLOAD_VIDEO -> handleVideoUpload(intent)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun handleVideoUpload(intent: Intent) {
        val videoUri = intent.getParcelableExtra<Uri>(EXTRA_VIDEO_URI)
        val uploadUrl = intent.getStringExtra(EXTRA_UPLOAD_URL)

        if (videoUri == null || uploadUrl == null) {
            return
        }

        serviceScope.launch {
            try {
                val result = videoUploadService.uploadVideo(contentResolver, videoUri, uploadUrl)
                result.fold(
                    onSuccess = { response ->
                        // Process the upload response with ChatGPT
                        processChatGPTResponse(response)
                    },
                    onFailure = { exception ->
                        // Handle upload failure
                        updateNotification("Upload failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                updateNotification("Error: ${e.message}")
            }
        }
    }

    private suspend fun processChatGPTResponse(uploadResponse: String) {
        try {
            val openAI = OpenAIConfig.getClient()
            val chatCompletion: ChatCompletion = openAI.chatCompletion(
                ChatCompletionRequest(
                    model = ModelId("gpt-4"),
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
                            content = "Process this upload response: $uploadResponse"
                        )
                    )
                )
            )
            
            val response = chatCompletion.choices.first().message.content
            updateNotification("Analysis complete: $response")
        } catch (e: Exception) {
            updateNotification("Analysis failed: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Dragon Suite Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background service for Dragon Suite"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForeground() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Dragon Suite")
            .setContentText("Running in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(message: String) {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Dragon Suite")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
