package com.example.dragonsuite

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.net.Uri
import com.example.dragonsuite.service.ChatGPTService
import com.example.dragonsuite.service.VideoPostManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class DragonService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var chatGPTService: ChatGPTService
    private lateinit var videoPostManager: VideoPostManager

    override fun onCreate() {
        super.onCreate()
        chatGPTService = ChatGPTService(this)
        videoPostManager = VideoPostManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle incoming commands
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_ANALYZE_VIDEO -> {
                val videoUri = intent.getParcelableExtra<Uri>(EXTRA_VIDEO_URI)
                if (videoUri != null) {
                    analyzeVideo(videoUri)
                }
            }
            ACTION_POST_VIDEO -> {
                val videoUri = intent.getParcelableExtra<Uri>(EXTRA_VIDEO_URI)
                if (videoUri != null) {
                    postVideo(videoUri)
                }
            }
        }
    }

    private fun analyzeVideo(videoUri: Uri) {
        serviceScope.launch {
            try {
                // Start video analysis process
                chatGPTService.startVideoAnalysis(videoUri)
                // The rest of the analysis will be handled through the ChatGPTService state machine
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun postVideo(videoUri: Uri) {
        serviceScope.launch {
            try {
                // Start video analysis process
                chatGPTService.startVideoAnalysis(videoUri)
                // The rest of the posting will be handled through the ChatGPTService state machine
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_ANALYZE_VIDEO = "com.example.dragonsuite.action.ANALYZE_VIDEO"
        const val ACTION_POST_VIDEO = "com.example.dragonsuite.action.POST_VIDEO"
        const val EXTRA_VIDEO_URI = "com.example.dragonsuite.extra.VIDEO_URI"
    }
}
