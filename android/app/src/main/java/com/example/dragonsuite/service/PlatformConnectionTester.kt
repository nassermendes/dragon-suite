package com.example.dragonsuite.service

import android.content.Context
import com.example.dragonsuite.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.example.dragonsuite.model.Platform
import com.example.dragonsuite.model.Account
import com.example.dragonsuite.model.ConnectionTestResult

data class ConnectionTestResult(
    val platform: Platform,
    val account: Account,
    val isConnected: Boolean,
    val accountName: String?,
    val error: String?
)

class PlatformConnectionTester(private val context: Context) {
    private val client = OkHttpClient.Builder().build()

    private val platformEndpoints = mapOf(
        Platform.INSTAGRAM_REELS to mapOf(
            Account.THEREAL_MENDES to "https://graph.instagram.com/v12.0/me",
            Account.ALGARVIOCHARITY to "https://graph.instagram.com/v12.0/me"
        ),
        Platform.YOUTUBE_SHORTS to mapOf(
            Account.THEREAL_MENDES to "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true",
            Account.ALGARVIOCHARITY to "https://www.googleapis.com/youtube/v3/channels?part=snippet&mine=true"
        ),
        Platform.TIKTOK to mapOf(
            Account.THEREAL_MENDES to "https://open-api.tiktok.com/v2/user/info/",
            Account.ALGARVIOCHARITY to "https://open-api.tiktok.com/v2/user/info/"
        )
    )

    private val platformTokens = mapOf(
        Platform.INSTAGRAM_REELS to mapOf(
            Account.THEREAL_MENDES to BuildConfig.INSTAGRAM_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.INSTAGRAM_TOKEN_CHARITY
        ),
        Platform.YOUTUBE_SHORTS to mapOf(
            Account.THEREAL_MENDES to BuildConfig.YOUTUBE_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.YOUTUBE_TOKEN_CHARITY
        ),
        Platform.TIKTOK to mapOf(
            Account.THEREAL_MENDES to BuildConfig.TIKTOK_TOKEN_MENDES,
            Account.ALGARVIOCHARITY to BuildConfig.TIKTOK_TOKEN_CHARITY
        )
    )

    suspend fun testAllConnections(): List<ConnectionTestResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ConnectionTestResult>()
        
        Platform.values().forEach { platform ->
            Account.values().forEach { account ->
                results.add(testConnection(platform, account))
            }
        }
        
        results
    }

    private suspend fun testConnection(platform: Platform, account: Account): ConnectionTestResult {
        val endpoint = platformEndpoints[platform]?.get(account)
            ?: return ConnectionTestResult(platform, account, false, null, "No endpoint configured")
        
        val token = platformTokens[platform]?.get(account)
            ?: return ConnectionTestResult(platform, account, false, null, "No token configured")

        if (token.isEmpty()) {
            return ConnectionTestResult(platform, account, false, null, "Token not set")
        }

        return try {
            val request = Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    ConnectionTestResult(
                        platform = platform,
                        account = account,
                        isConnected = false,
                        accountName = null,
                        error = "HTTP ${response.code}: ${response.message}"
                    )
                } else {
                    val responseBody = response.body?.string()
                    val accountInfo = parseAccountInfo(platform, responseBody)
                    ConnectionTestResult(
                        platform = platform,
                        account = account,
                        isConnected = true,
                        accountName = accountInfo,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            ConnectionTestResult(
                platform = platform,
                account = account,
                isConnected = false,
                accountName = null,
                error = e.message
            )
        }
    }

    private fun parseAccountInfo(platform: Platform, response: String?): String? {
        if (response == null) return null
        
        return try {
            val json = JSONObject(response)
            when (platform) {
                Platform.INSTAGRAM_REELS -> json.getString("username")
                Platform.YOUTUBE_SHORTS -> json.getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet")
                    .getString("title")
                Platform.TIKTOK -> json.getJSONObject("data")
                    .getJSONObject("user")
                    .getString("display_name")
            }
        } catch (e: Exception) {
            null
        }
    }

    fun formatResults(results: List<ConnectionTestResult>): String {
        return buildString {
            appendLine("Platform Connection Test Results:")
            appendLine("================================")
            
            Platform.values().forEach { platform ->
                appendLine("\n${platform.name}:")
                results.filter { it.platform == platform }.forEach { result ->
                    val status = if (result.isConnected) "✓" else "✗"
                    val accountInfo = result.accountName?.let { " ($it)" } ?: ""
                    val error = result.error?.let { " - Error: $it" } ?: ""
                    appendLine("  $status ${result.account}$accountInfo$error")
                }
            }
        }
    }
}
