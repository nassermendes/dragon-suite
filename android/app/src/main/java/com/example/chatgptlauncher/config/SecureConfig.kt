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

    fun storeCredentials() {
        with(securePrefs.edit()) {
            // OpenAI
            putString("OPENAI_API_KEY", "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")

            // YouTube Personal
            putString("YOUTUBE_PERSONAL_CLIENT_ID", "1086175330509-7a8otqh6boj4s0a84onnajk0f2pbon97.apps.googleusercontent.com")
            putString("YOUTUBE_PERSONAL_CLIENT_SECRET", "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")

            // YouTube Charity (same as personal in this case)
            putString("YOUTUBE_CHARITY_CLIENT_ID", "1086175330509-7a8otqh6boj4s0a84onnajk0f2pbon97.apps.googleusercontent.com")
            putString("YOUTUBE_CHARITY_CLIENT_SECRET", "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")

            // Instagram Personal
            putString("INSTAGRAM_PERSONAL_ACCESS_TOKEN", "IGAAXem4IwHs9BZAE1fVGgxSXF6ZA2gxdzZAUTEhOVm5idElveU15aXh3SnktbnlBVDNNWjdBSWVwY1VQSElSX3dDckE5WnYwNWdxZA0hiT2oyS3F1aU5VVFpyZAU5ySWdLUU1vYlBydnpTNDZAMeHR3bTNMQm1QeWFrQXhjRG9hZAXVfbwZDZD")
            putString("INSTAGRAM_PERSONAL_USER_ID", "17841402078003768")

            // Instagram Charity
            putString("INSTAGRAM_CHARITY_ACCESS_TOKEN", "IGAAXem4IwHs9BZAE84S1pOS0lFZA1lpN2tDenQzUkNMVUR0ZAExzaUEtQUFEVG4xNnctQjFPNmhRbXZASTEVrbVkzd25XemZAsaEhSQk14ZA0taMS1BYVozQjVMYXlYdmh0Mk91N1NKdHQ3QV95SFQ3WHdQcTRiUFJ2XzhhWkdqLThpOAZDZD")
            putString("INSTAGRAM_CHARITY_USER_ID", "17841469750108240")

            // Instagram App
            putString("INSTAGRAM_APP_ID", "1652134369042127")
            putString("INSTAGRAM_APP_SECRET", "e4c9d3340826ed90f625b8d52c538f92")

            // TikTok App
            putString("TIKTOK_APP_KEY", "awdmgl4x0u4626up")
            putString("TIKTOK_APP_SECRET", "hUtphXRT4J1X8I4ro09THiHvUHoFCLp0")

            apply()
        }
    }

    fun getCredential(key: String): String? = securePrefs.getString(key, null)
}
