package com.example.chatgptlauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.chatgptlauncher.services.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var uploadManager: UploadManager
    private lateinit var messageView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        messageView = findViewById(R.id.messageView)
        
        // Initialize services
        val videoProcessor = VideoProcessor(this)
        val contentGenerator = ContentGenerator()
        uploadManager = UploadManager(this, videoProcessor, contentGenerator)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data
                if (uri != null) {
                    val action = uri.lastPathSegment ?: "unknown"
                    val params = mutableMapOf<String, String>()
                    uri.queryParameterNames.forEach { name ->
                        params[name] = uri.getQueryParameter(name) ?: ""
                    }
                    
                    when (action) {
                        "upload" -> handleUpload(params)
                        "speak" -> {
                            val text = params["text"] ?: "No text provided"
                            messageView.text = "Speaking: $text"
                            // TODO: Implement text-to-speech
                        }
                        "open" -> {
                            val url = params["url"]
                            if (url != null) {
                                messageView.text = "Opening URL: $url"
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            } else {
                                messageView.text = "No URL provided"
                            }
                        }
                        else -> {
                            messageView.text = "Received action: $action\nParameters: $params"
                        }
                    }
                } else {
                    messageView.text = "Welcome to ChatGPT Launcher"
                }
            }
            else -> {
                messageView.text = "Welcome to ChatGPT Launcher\nReady to receive commands!"
            }
        }
    }

    private fun handleUpload(params: Map<String, String>) {
        val videoUri = params["video"]?.let { Uri.parse(it) }
        val platformsJson = params["platforms"]
        val isCharity = params["isCharity"]?.toBoolean() ?: false
        val context = params["context"] ?: ""

        if (videoUri == null || platformsJson == null) {
            messageView.text = "Missing required parameters: video URI or platforms"
            return
        }

        try {
            val platforms = JSONArray(platformsJson).let { json ->
                (0 until json.length()).map { i ->
                    SocialPlatform.valueOf(json.getString(i))
                }
            }

            lifecycleScope.launch {
                uploadManager.uploadVideo(videoUri, platforms, isCharity, context)
                    .collect { progressMap ->
                        val status = JSONObject().apply {
                            put("status", "uploading")
                            put("progress", JSONObject().apply {
                                progressMap.forEach { (platform, progress) ->
                                    put(platform.name, JSONObject().apply {
                                        put("stage", progress.stage.name)
                                        put("progress", progress.progress)
                                        put("message", progress.message ?: "")
                                    })
                                }
                            })
                        }
                        
                        messageView.text = status.toString(2)
                    }
            }
        } catch (e: Exception) {
            messageView.text = "Error: ${e.message}"
        }
    }
}
