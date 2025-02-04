package com.example.dragonsuite.service

import android.content.Intent
import android.net.Uri
import com.example.dragonsuite.DragonService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class DragonFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val ACTION_ANALYZE_VIDEO = "analyze_video"
        private const val KEY_ACTION = "action"
        private const val KEY_VIDEO_URI = "video_uri"
        private const val KEY_UPLOAD_URL = "upload_url"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Extract data from the message
        val data = message.data
        when (data[KEY_ACTION]) {
            ACTION_ANALYZE_VIDEO -> handleVideoAnalysis(data)
            else -> {
                // Handle unknown action
                println("Unknown action received: ${data[KEY_ACTION]}")
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send this token to your server to enable FCM messaging
        println("New FCM token: $token")
    }

    private fun handleVideoAnalysis(data: Map<String, String>) {
        val videoUri = data[KEY_VIDEO_URI]?.let { Uri.parse(it) }
        val uploadUrl = data[KEY_UPLOAD_URL]

        if (videoUri == null || uploadUrl == null) {
            println("Invalid video analysis request: missing uri or upload url")
            return
        }

        // Start the DragonService with the video analysis intent
        val intent = Intent(this, DragonService::class.java).apply {
            action = DragonService.ACTION_UPLOAD_VIDEO
            putExtra(DragonService.EXTRA_VIDEO_URI, videoUri)
            putExtra(DragonService.EXTRA_UPLOAD_URL, uploadUrl)
        }
        startService(intent)
    }
}
