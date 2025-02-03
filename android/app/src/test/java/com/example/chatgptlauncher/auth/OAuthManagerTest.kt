package com.example.chatgptlauncher.auth

import android.content.Context
import android.content.Intent
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.model.SocialPlatform
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OAuthManagerTest {
    private lateinit var oAuthManager: OAuthManager
    private lateinit var context: Context
    private lateinit var secureConfig: SecureConfig
    private lateinit var loginManager: LoginManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        secureConfig = mockk(relaxed = true)
        loginManager = mockk(relaxed = true)
        
        mockkStatic(LoginManager::class)
        every { LoginManager.getInstance() } returns loginManager

        oAuthManager = OAuthManager(context, secureConfig)
    }

    @Test
    fun `authenticateYouTube should handle successful flow creation`() = runTest {
        // Mock YouTube credentials
        every { secureConfig.getYouTubePersonalClientId() } returns "test-client-id"
        every { secureConfig.getYouTubePersonalClientSecret() } returns "test-client-secret"

        val result = oAuthManager.authenticateYouTube(isCharityAccount = false)
        
        assertThat(result).isInstanceOf(OAuthManager.AuthResult.Success::class.java)
        val success = result as OAuthManager.AuthResult.Success
        assertThat(success.platform).isEqualTo(SocialPlatform.YOUTUBE)
        
        verify {
            context.startActivity(any())
        }
    }

    @Test
    fun `authenticateYouTube should handle errors`() = runTest {
        every { secureConfig.getYouTubePersonalClientId() } throws Exception("Invalid client")

        val result = oAuthManager.authenticateYouTube(isCharityAccount = false)
        
        assertThat(result).isInstanceOf(OAuthManager.AuthResult.Error::class.java)
        val error = result as OAuthManager.AuthResult.Error
        assertThat(error.platform).isEqualTo(SocialPlatform.YOUTUBE)
        assertThat(error.message).contains("Failed to authenticate")
    }

    @Test
    fun `authenticateInstagram should handle successful login`() = runTest {
        val callbackSlot = slot<FacebookCallback<LoginResult>>()
        every { 
            loginManager.registerCallback(any(), capture(callbackSlot))
        } just Runs

        // Start authentication
        val authJob = coEvery { 
            oAuthManager.authenticateInstagram(isCharityAccount = false)
        }

        // Simulate successful callback
        val mockAccessToken = mockk<AccessToken> {
            every { token } returns "test-token"
        }
        val mockLoginResult = mockk<LoginResult> {
            every { accessToken } returns mockAccessToken
        }
        callbackSlot.captured.onSuccess(mockLoginResult)

        val result = authJob.result()
        assertThat(result).isInstanceOf(OAuthManager.AuthResult.Success::class.java)
        
        verify {
            secureConfig.setInstagramPersonalAccessToken("test-token")
        }
    }

    @Test
    fun `authenticateInstagram should handle login cancellation`() = runTest {
        val callbackSlot = slot<FacebookCallback<LoginResult>>()
        every { 
            loginManager.registerCallback(any(), capture(callbackSlot))
        } just Runs

        // Start authentication
        val authJob = coEvery { 
            oAuthManager.authenticateInstagram(isCharityAccount = false)
        }

        // Simulate cancellation
        callbackSlot.captured.onCancel()

        val result = authJob.result()
        assertThat(result).isInstanceOf(OAuthManager.AuthResult.Error::class.java)
        val error = result as OAuthManager.AuthResult.Error
        assertThat(error.message).contains("cancelled")
    }

    @Test
    fun `authenticateTikTok should create correct auth URL`() = runTest {
        every { secureConfig.getTikTokAppKey() } returns "test-app-key"

        val result = oAuthManager.authenticateTikTok()
        
        assertThat(result).isInstanceOf(OAuthManager.AuthResult.Success::class.java)
        
        verify {
            context.startActivity(match { intent ->
                intent.data.toString().contains("client_key=test-app-key") &&
                intent.data.toString().contains("scope=video.upload,video.list")
            })
        }
    }

    @Test
    fun `handleOAuthCallback should delegate to callback manager`() {
        val requestCode = 123
        val resultCode = 456
        val data = mockk<Intent>()

        oAuthManager.handleOAuthCallback(requestCode, resultCode, data)

        verify {
            loginManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}
