package com.example.chatgptlauncher.config

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecureConfig(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val securePrefs = EncryptedSharedPreferences.create(
        "secure_social_media_config",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // OpenAI
    fun getOpenAIApiKey(): String = securePrefs.getString("OPENAI_API_KEY", "") ?: ""
    fun setOpenAIApiKey(key: String) = securePrefs.edit().putString("OPENAI_API_KEY", key).apply()

    // YouTube Personal
    fun getYouTubePersonalClientId(): String = securePrefs.getString("YOUTUBE_PERSONAL_CLIENT_ID", "") ?: ""
    fun setYouTubePersonalClientId(id: String) = securePrefs.edit().putString("YOUTUBE_PERSONAL_CLIENT_ID", id).apply()
    fun getYouTubePersonalClientSecret(): String = securePrefs.getString("YOUTUBE_PERSONAL_CLIENT_SECRET", "") ?: ""
    fun setYouTubePersonalClientSecret(secret: String) = securePrefs.edit().putString("YOUTUBE_PERSONAL_CLIENT_SECRET", secret).apply()

    // YouTube Charity
    fun getYouTubeCharityClientId(): String = securePrefs.getString("YOUTUBE_CHARITY_CLIENT_ID", "") ?: ""
    fun setYouTubeCharityClientId(id: String) = securePrefs.edit().putString("YOUTUBE_CHARITY_CLIENT_ID", id).apply()
    fun getYouTubeCharityClientSecret(): String = securePrefs.getString("YOUTUBE_CHARITY_CLIENT_SECRET", "") ?: ""
    fun setYouTubeCharityClientSecret(secret: String) = securePrefs.edit().putString("YOUTUBE_CHARITY_CLIENT_SECRET", secret).apply()

    // Instagram Personal
    fun getInstagramPersonalAccessToken(): String = securePrefs.getString("INSTAGRAM_PERSONAL_ACCESS_TOKEN", "") ?: ""
    fun setInstagramPersonalAccessToken(token: String) = securePrefs.edit().putString("INSTAGRAM_PERSONAL_ACCESS_TOKEN", token).apply()
    fun getInstagramPersonalUserId(): String = securePrefs.getString("INSTAGRAM_PERSONAL_USER_ID", "") ?: ""
    fun setInstagramPersonalUserId(id: String) = securePrefs.edit().putString("INSTAGRAM_PERSONAL_USER_ID", id).apply()

    // Instagram Charity
    fun getInstagramCharityAccessToken(): String = securePrefs.getString("INSTAGRAM_CHARITY_ACCESS_TOKEN", "") ?: ""
    fun setInstagramCharityAccessToken(token: String) = securePrefs.edit().putString("INSTAGRAM_CHARITY_ACCESS_TOKEN", token).apply()
    fun getInstagramCharityUserId(): String = securePrefs.getString("INSTAGRAM_CHARITY_USER_ID", "") ?: ""
    fun setInstagramCharityUserId(id: String) = securePrefs.edit().putString("INSTAGRAM_CHARITY_USER_ID", id).apply()

    // Instagram App
    fun getInstagramAppId(): String = securePrefs.getString("INSTAGRAM_APP_ID", "") ?: ""
    fun setInstagramAppId(id: String) = securePrefs.edit().putString("INSTAGRAM_APP_ID", id).apply()
    fun getInstagramAppSecret(): String = securePrefs.getString("INSTAGRAM_APP_SECRET", "") ?: ""
    fun setInstagramAppSecret(secret: String) = securePrefs.edit().putString("INSTAGRAM_APP_SECRET", secret).apply()

    // TikTok
    fun getTikTokAppKey(): String = securePrefs.getString("TIKTOK_APP_KEY", "") ?: ""
    fun setTikTokAppKey(key: String) = securePrefs.edit().putString("TIKTOK_APP_KEY", key).apply()
    fun getTikTokAppSecret(): String = securePrefs.getString("TIKTOK_APP_SECRET", "") ?: ""
    fun setTikTokAppSecret(secret: String) = securePrefs.edit().putString("TIKTOK_APP_SECRET", secret).apply()
    fun getTikTokAccessToken(): String = securePrefs.getString("TIKTOK_ACCESS_TOKEN", "") ?: ""
    fun setTikTokAccessToken(token: String) = securePrefs.edit().putString("TIKTOK_ACCESS_TOKEN", token).apply()
}
