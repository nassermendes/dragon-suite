package com.example.dragonsuite.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.tiktok.open.sdk.TikTokOpenApiFactory
import com.tiktok.open.sdk.api.TikTokOpenApi
import com.tiktok.open.sdk.auth.AuthRequest
import com.tiktok.open.sdk.auth.AuthResponse
import com.tiktok.open.sdk.share.ShareRequest

object TikTokUtils {
    private const val TAG = "TikTokUtils"
    private lateinit var tikTokApi: TikTokOpenApi
    
    fun initialize(context: Context, clientKey: String) {
        tikTokApi = TikTokOpenApiFactory.create(context)
        Log.d(TAG, "TikTok SDK initialized")
    }
    
    fun authorize(context: Context, scope: String, callback: (AuthResponse?) -> Unit) {
        val request = AuthRequest().apply {
            this.scope = scope
            callerLocalEntry = "com.example.dragonsuite.TikTokTestActivity"
        }
        
        tikTokApi.auth(request) { response ->
            if (response.isSuccess) {
                Log.d(TAG, "Auth success: ${response.authCode}")
                callback(response)
            } else {
                Log.e(TAG, "Auth failed: ${response.errorMsg}")
                callback(null)
            }
        }
    }
    
    fun shareVideo(context: Context, videoPath: String, callback: (Boolean) -> Unit) {
        val request = ShareRequest().apply {
            this.mediaPath = videoPath
            callerLocalEntry = "com.example.dragonsuite.TikTokTestActivity"
        }
        
        tikTokApi.share(request) { response ->
            if (response.isSuccess) {
                Log.d(TAG, "Share success")
                callback(true)
            } else {
                Log.e(TAG, "Share failed: ${response.errorMsg}")
                callback(false)
            }
        }
    }
    
    fun getAppSignatures(context: Context): Array<String> {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            return Array(packageInfo.signatures.size) { i ->
                packageInfo.signatures[i].toCharsString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app signatures", e)
            return emptyArray()
        }
    }
}