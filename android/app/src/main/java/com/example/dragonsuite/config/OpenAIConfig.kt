package com.example.dragonsuite.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

object OpenAIConfig {
    private var openAI: OpenAI? = null
    
    fun initialize(apiKey: String) {
        openAI = OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds)
        )
    }
    
    fun getClient(): OpenAI {
        return openAI ?: throw IllegalStateException("OpenAI client not initialized. Call initialize() first.")
    }
}
