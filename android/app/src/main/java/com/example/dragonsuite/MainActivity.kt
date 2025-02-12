package com.example.dragonsuite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.dragonsuite.config.TikTokConfig
import com.example.dragonsuite.utils.TikTokUtils
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Get FCM token
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM Token: $token")
                } else {
                    Log.w(TAG, "Failed to get FCM token", task.exception)
                }
            }

        // Initialize TikTok SDK
        TikTokUtils.initialize(this, TikTokConfig.CLIENT_KEY)
        
        val testTikTokButton = Button(this).apply {
            text = "Test TikTok Integration"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, TikTokTestActivity::class.java))
            }
        }
        
        // Add TikTok test button to layout
        findViewById<LinearLayout>(R.id.buttonContainer).addView(testTikTokButton)

        // Start the Dragon Service
        startService(android.content.Intent(this, DragonService::class.java))
        
        // Close the activity immediately
        finish()
    }
}
