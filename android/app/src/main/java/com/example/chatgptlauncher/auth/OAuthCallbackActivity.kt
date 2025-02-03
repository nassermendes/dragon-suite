package com.example.chatgptlauncher.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class OAuthCallbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        when {
            response != null -> {
                // Handle successful authorization
                // Store the auth code or token
                setResult(RESULT_OK)
            }
            error != null -> {
                // Handle authorization error
                setResult(RESULT_CANCELED)
            }
        }
        
        finish()
    }
}
