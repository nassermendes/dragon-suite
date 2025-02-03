package com.example.chatgptlauncher.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.model.SocialPlatform
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTubeScopes
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OAuthManager(
    private val context: Context,
    private val secureConfig: SecureConfig
) {
    sealed class AuthResult {
        data class Success(val platform: SocialPlatform) : AuthResult()
        data class Error(
            val platform: SocialPlatform,
            val message: String,
            val exception: Exception? = null
        ) : AuthResult()
    }

    private val callbackManager = CallbackManager.Factory.create()
    
    // YouTube OAuth
    private fun createYouTubeFlow(isCharityAccount: Boolean): GoogleAuthorizationCodeFlow {
        val clientId = if (isCharityAccount) {
            secureConfig.getYouTubeCharityClientId()
        } else {
            secureConfig.getYouTubePersonalClientId()
        }

        val clientSecret = if (isCharityAccount) {
            secureConfig.getYouTubeCharityClientSecret()
        } else {
            secureConfig.getYouTubePersonalClientSecret()
        }

        val clientSecrets = GoogleClientSecrets().apply {
            installed = GoogleClientSecrets.Details().apply {
                this.clientId = clientId
                this.clientSecret = clientSecret
            }
        }

        return GoogleAuthorizationCodeFlow.Builder(
            NetHttpTransport(),
            GsonFactory(),
            clientSecrets,
            listOf(YouTubeScopes.YOUTUBE_UPLOAD)
        )
        .setAccessType("offline")
        .build()
    }

    suspend fun authenticateYouTube(isCharityAccount: Boolean): AuthResult {
        return try {
            val flow = createYouTubeFlow(isCharityAccount)
            
            // This will open the browser for authentication
            val credential = flow.loadCredential("user")
            
            if (credential?.accessToken == null) {
                val authUrl = flow.newAuthorizationUrl()
                    .setRedirectUri("urn:ietf:wg:oauth:2.0:oob")
                    .build()
                    
                // Open browser for authentication
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)))
            }
            
            AuthResult.Success(SocialPlatform.YOUTUBE)
        } catch (e: Exception) {
            AuthResult.Error(SocialPlatform.YOUTUBE, "Failed to authenticate with YouTube", e)
        }
    }

    // Instagram OAuth
    suspend fun authenticateInstagram(isCharityAccount: Boolean): AuthResult {
        return suspendCancellableCoroutine { continuation ->
            try {
                LoginManager.getInstance().apply {
                    registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult) {
                            val accessToken = result.accessToken.token
                            if (isCharityAccount) {
                                secureConfig.setInstagramCharityAccessToken(accessToken)
                            } else {
                                secureConfig.setInstagramPersonalAccessToken(accessToken)
                            }
                            continuation.resume(AuthResult.Success(SocialPlatform.INSTAGRAM))
                        }

                        override fun onCancel() {
                            continuation.resume(AuthResult.Error(
                                SocialPlatform.INSTAGRAM,
                                "Authentication cancelled by user"
                            ))
                        }

                        override fun onError(error: FacebookException) {
                            continuation.resumeWithException(error)
                        }
                    })

                    logInWithReadPermissions(
                        context.activity,
                        listOf("instagram_basic", "instagram_content_publish")
                    )
                }
            } catch (e: Exception) {
                continuation.resume(AuthResult.Error(
                    SocialPlatform.INSTAGRAM,
                    "Failed to authenticate with Instagram",
                    e
                ))
            }
        }
    }

    // TikTok OAuth
    suspend fun authenticateTikTok(): AuthResult {
        return try {
            val appKey = secureConfig.getTikTokAppKey()
            val redirectUri = "dragonsuite://oauth/callback"
            
            val authUrl = "https://www.tiktok.com/auth/authorize/" +
                "?client_key=$appKey" +
                "&scope=video.upload,video.list" +
                "&response_type=code" +
                "&redirect_uri=$redirectUri"
            
            // Open browser for authentication
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)))
            
            AuthResult.Success(SocialPlatform.TIKTOK)
        } catch (e: Exception) {
            AuthResult.Error(SocialPlatform.TIKTOK, "Failed to authenticate with TikTok", e)
        }
    }

    fun handleOAuthCallback(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
