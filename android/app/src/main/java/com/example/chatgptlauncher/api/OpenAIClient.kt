package com.example.chatgptlauncher.api

import com.example.chatgptlauncher.config.SecureConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") auth: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String = "gpt-4",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

class OpenAIClient(private val secureConfig: SecureConfig) {
    private val service: OpenAIService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(OpenAIService::class.java)
    }

    suspend fun generateSocialMediaContent(
        platform: String,
        isCharity: Boolean,
        videoContext: String
    ): String {
        val apiKey = secureConfig.getCredential("OPENAI_API_KEY")
            ?: throw IllegalStateException("OpenAI API key not found")

        val prompt = """
            Generate engaging social media content for a ${if (isCharity) "charity" else "personal"} account on $platform.
            Video context: $videoContext
            
            Please provide:
            1. A catchy caption
            2. Relevant hashtags
            3. Any platform-specific optimizations
            
            Format the response as JSON with the following structure:
            {
                "caption": "The main caption text",
                "hashtags": ["tag1", "tag2", ...],
                "title": "Optional title if needed"
            }
        """.trimIndent()

        val request = ChatCompletionRequest(
            messages = listOf(
                Message("system", "You are a social media content expert."),
                Message("user", prompt)
            )
        )

        val response = service.generateContent(
            auth = "Bearer $apiKey",
            request = request
        )

        return response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("No content generated")
    }
}
