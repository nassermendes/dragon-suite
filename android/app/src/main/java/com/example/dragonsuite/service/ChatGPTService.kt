package com.example.dragonsuite.service

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.example.dragonsuite.config.OpenAIConfig
import com.example.dragonsuite.DragonService
import com.example.dragonsuite.model.UploadResult
import com.example.dragonsuite.model.VideoAnalysis
import com.example.dragonsuite.model.Platform
import com.example.dragonsuite.model.Account
import com.example.dragonsuite.model.ConnectionTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.Result

enum class ConversationState {
    IDLE,
    WAITING_FOR_TITLE,
    WAITING_FOR_DESCRIPTION,
    WAITING_FOR_HASHTAGS,
    WAITING_FOR_SCHEDULE,
    WAITING_FOR_CONFIRMATION
}

class ChatGPTService(private val context: Context) {
    private val openAI = OpenAIConfig.getClient()
    private val videoPostManager = VideoPostManager(context)
    private var currentState = ConversationState.IDLE
    private var currentVideoUri: Uri? = null
    private var currentTitle: String? = null
    private var currentDescription: String? = null
    private var currentHashtags: List<String>? = null
    private var currentScheduledTime: LocalDateTime? = null

    suspend fun streamResponse(userMessage: String): Flow<String> = flow {
        // Handle special commands
        when {
            userMessage.trim().equals("test connections", ignoreCase = true) -> {
                emit("Testing social media connections...\n")
                val results = testConnections()
                emit(formatResults(results))
                return@flow
            }
            // Add other special commands here
        }

        // Regular chat processing
        val messages = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant that helps users manage their social media video posts."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = userMessage
            )
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = messages
        )

        try {
            val completion = openAI.chatCompletion(chatCompletionRequest)
            val response = completion.choices.firstOrNull()?.message?.content ?: "No response"
            emit(response)
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }

    suspend fun testConnections(): Result<List<ConnectionTestResult>> = withContext(Dispatchers.IO) {
        try {
            val tester = PlatformConnectionTester(context)
            val results = tester.testAllConnections()
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postVideo(videoUri: Uri, title: String, description: String, hashtags: List<String>): Result<List<UploadResult>> = withContext(Dispatchers.IO) {
        try {
            val videoManager = VideoPostManager(context)
            val results = videoManager.postVideo(videoUri, title, description, hashtags)
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun startVideoAnalysis(videoUri: Uri) {
        this.currentVideoUri = videoUri
        currentState = ConversationState.WAITING_FOR_TITLE
    }

    fun getCurrentState(): ConversationState = currentState

    fun resetState() {
        currentState = ConversationState.IDLE
        currentVideoUri = null
        currentTitle = null
        currentDescription = null
        currentHashtags = null
        currentScheduledTime = null
    }

    fun setTitle(title: String) {
        currentTitle = title
        currentState = ConversationState.WAITING_FOR_DESCRIPTION
    }

    fun setDescription(description: String) {
        currentDescription = description
        currentState = ConversationState.WAITING_FOR_HASHTAGS
    }

    fun setHashtags(hashtags: List<String>) {
        currentHashtags = hashtags
        currentState = ConversationState.WAITING_FOR_SCHEDULE
    }

    fun setScheduledTime(timeStr: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            currentScheduledTime = LocalDateTime.parse(timeStr, formatter)
            currentState = ConversationState.WAITING_FOR_CONFIRMATION
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    suspend fun confirmAndPost(): kotlin.Result<List<UploadResult>> {
        val uri = currentVideoUri
        val title = currentTitle
        val description = currentDescription
        val hashtags = currentHashtags
        val scheduledTime = currentScheduledTime

        if (uri == null || title == null || description == null || hashtags == null) {
            return kotlin.Result.failure(IllegalStateException("Missing required video information"))
        }

        return try {
            val result = if (scheduledTime != null) {
                videoPostManager.scheduleVideo(
                    context.contentResolver,
                    uri,
                    title,
                    description,
                    hashtags,
                    scheduledTime
                )
            } else {
                videoPostManager.postNow(
                    context.contentResolver,
                    uri,
                    title,
                    description,
                    hashtags
                )
            }

            // Reset state after successful post
            resetState()
            kotlin.Result.success(result)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    private fun formatResults(results: Result<List<ConnectionTestResult>>): String {
        return when (results) {
            is Result.Success -> results.value.joinToString("\n") { it.toString() }
            is Result.Failure -> "Error: ${results.exception.message}"
        }
    }
}
