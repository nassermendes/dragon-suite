package com.example.dragonsuite.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureKeyManager(private val context: Context) {
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val MAIN_KEY_ALIAS = "DragonSuiteMainKey"
        private const val SHARED_PREFS_NAME = "DragonSuiteSecurePrefs"
        private const val OPENAI_KEY_PREF = "encrypted_openai_key"
        private const val IV_PREF = "openai_key_iv"
    }

    private val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    init {
        if (!keyStore.containsAlias(MAIN_KEY_ALIAS)) {
            createMainKey()
        }
    }

    private fun createMainKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            MAIN_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    fun storeOpenAIKey(apiKey: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = keyStore.getKey(MAIN_KEY_ALIAS, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encrypted = cipher.doFinal(apiKey.toByteArray())
        val iv = cipher.iv

        sharedPreferences.edit()
            .putString(OPENAI_KEY_PREF, Base64.encodeToString(encrypted, Base64.DEFAULT))
            .putString(IV_PREF, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }

    fun getOpenAIKey(): String? {
        val encryptedKey = sharedPreferences.getString(OPENAI_KEY_PREF, null)
        val iv = sharedPreferences.getString(IV_PREF, null)

        if (encryptedKey == null || iv == null) {
            return null
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = keyStore.getKey(MAIN_KEY_ALIAS, null) as SecretKey
        val spec = GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decrypted = cipher.doFinal(Base64.decode(encryptedKey, Base64.DEFAULT))
        return String(decrypted)
    }
}
