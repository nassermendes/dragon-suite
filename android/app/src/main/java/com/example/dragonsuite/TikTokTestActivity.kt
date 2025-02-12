package com.example.dragonsuite

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dragonsuite.config.TikTokConfig
import com.example.dragonsuite.utils.TikTokUtils

class TikTokTestActivity : AppCompatActivity() {
    private val TAG = "TikTokTestActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiktok_test)
        
        // Initialize TikTok SDK
        TikTokUtils.initialize(this, TikTokConfig.CLIENT_KEY)
        
        // Set up UI elements
        val statusText = findViewById<TextView>(R.id.statusText)
        val authButton = findViewById<Button>(R.id.authButton)
        
        // Set up auth button
        authButton.setOnClickListener {
            statusText.text = "Authenticating..."
            TikTokUtils.authorize(this, TikTokConfig.ALL_SCOPES) { response ->
                runOnUiThread {
                    if (response != null) {
                        val authCode = response.authCode
                        statusText.text = "Auth Success!\nAuth Code: $authCode"
                        Log.d(TAG, "Auth success with code: $authCode")
                    } else {
                        statusText.text = "Auth Failed"
                        Log.e(TAG, "Auth failed")
                    }
                }
            }
        }
        
        // Log app signatures for verification
        val signatures = TikTokUtils.getAppSignatures(this)
        signatures.forEach { signature ->
            Log.d(TAG, "App Signature: $signature")
        }
    }
}
