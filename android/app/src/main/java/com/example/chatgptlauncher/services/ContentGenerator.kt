package com.example.chatgptlauncher.services

import android.util.Log
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.model.SocialPlatform
import kotlin.time.Duration.Companion.seconds

class ContentGenerator(private val secureConfig: SecureConfig) {
    companion object {
        private const val TAG = "ContentGenerator"
        private const val MODEL = "gpt-4"
        private val TIMEOUT = 60.seconds
    }

    data class GeneratedContent(
        val caption: String,
        val hashtags: List<String>,
        val title: String? = null
    )

    sealed class GenerationError : Exception() {
        data class ApiError(override val message: String, override val cause: Throwable? = null) : GenerationError()
        data class InvalidResponse(override val message: String) : GenerationError()
        data class NoApiKey(override val message: String = "OpenAI API key not found") : GenerationError()
    }

    private val platformPrompts = mapOf(
        SocialPlatform.YOUTUBE to """
            Generate engaging YouTube content for this video. Format:
            Title: [Attention-grabbing title, max 100 characters]
            Description: [Engaging description with key points, max 5000 characters]
            Tags: [10-15 relevant hashtags]
        """.trimIndent(),
        
        SocialPlatform.INSTAGRAM to """
            Generate engaging Instagram content for this video. Format:
            Caption: [Engaging caption with emojis, max 2200 characters]
            Hashtags: [20-25 relevant hashtags]
        """.trimIndent(),
        
        SocialPlatform.TIKTOK to """
            Generate viral TikTok content for this video. Format:
            Caption: [Catchy caption with emojis, max 300 characters]
            Hashtags: [4-5 trending hashtags]
        """.trimIndent()
    )

    private suspend fun createOpenAIClient(): OpenAI {
        val apiKey = secureConfig.getOpenAIApiKey().takeIf { it.isNotEmpty() }
            ?: throw GenerationError.NoApiKey()
            
        return OpenAI(
            token = apiKey,
            timeout = Timeout(socket = TIMEOUT)
        )
    }

    suspend fun generateContent(
        platform: SocialPlatform,
        isCharityAccount: Boolean,
        videoContext: String
    ): GeneratedContent {
        try {
            val openAI = createOpenAIClient()
            
            val prompt = buildString {
                append(platformPrompts[platform])
                append("\n\nVideo Context: $videoContext")
                if (isCharityAccount) {
                    append("\n\nThis is for a charity account. Focus on impact, community, and calls to action for support.")
                }
            }

            val chatCompletion = openAI.chatCompletion(
                ChatCompletionRequest(
                    model = ModelId(MODEL),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = "You are a social media expert specializing in creating engaging, platform-optimized content."
                        ),
                        ChatMessage(
                            role = ChatRole.User,
                            content = prompt
                        )
                    )
                )
            )

            val response = chatCompletion.choices.firstOrNull()?.message?.content
                ?: throw GenerationError.InvalidResponse("Empty response from OpenAI")

            return parseResponse(platform, response)
        } catch (e: GenerationError) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content", e)
            throw GenerationError.ApiError("Failed to generate content", e)
        }
    }

    private fun parseResponse(platform: SocialPlatform, response: String): GeneratedContent {
        return when (platform) {
            SocialPlatform.YOUTUBE -> {
                val lines = response.lines()
                val title = lines.find { it.startsWith("Title:") }?.substringAfter(":")?.trim()
                val description = lines.find { it.startsWith("Description:") }?.substringAfter(":")?.trim()
                val tags = lines.find { it.startsWith("Tags:") }
                    ?.substringAfter(":")
                    ?.trim()
                    ?.split(" ")
                    ?.filter { it.startsWith("#") }
                    ?: emptyList()

                GeneratedContent(
                    caption = description ?: "",
                    hashtags = tags,
                    title = title
                )
            }
            
            SocialPlatform.INSTAGRAM, SocialPlatform.TIKTOK -> {
                val caption = response.lines()
                    .find { it.startsWith("Caption:") }
                    ?.substringAfter(":")
                    ?.trim()
                    ?: ""
                    
                val hashtags = response.lines()
                    .find { it.startsWith("Hashtags:") }
                    ?.substringAfter(":")
                    ?.trim()
                    ?.split(" ")
                    ?.filter { it.startsWith("#") }
                    ?: emptyList()

                GeneratedContent(
                    caption = caption,
                    hashtags = hashtags
                )
            }
        }
    }
}
