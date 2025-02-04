package com.example.dragonsuite.service

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.example.dragonsuite.config.OpenAIConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import android.util.Base64

class VideoAnalysisService {
    companion object {
        private const val FRAMES_TO_ANALYZE = 5 // Number of frames to sample from the video
    }

    suspend fun analyzeVideo(videoUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val frames = extractFrames(videoUri)
            val analysisResults = analyzeFramesWithGPT(frames)
            Result.success(analysisResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractFrames(videoUri: Uri): List<String> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoUri.toString())

        // Get video duration
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        val interval = duration / (FRAMES_TO_ANALYZE + 1) // +1 to avoid the very last frame

        return buildList {
            for (i in 1..FRAMES_TO_ANALYZE) {
                val timeUs = (interval * i * 1000) // Convert to microseconds
                val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                frame?.let {
                    add(convertBitmapToBase64(it))
                }
            }
        }.also {
            retriever.release()
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private suspend fun analyzeFramesWithGPT(frames: List<String>): String {
        val openAI = OpenAIConfig.getClient()
        
        val systemPrompt = """
            You are analyzing frames from a video. For each frame, provide:
            1. A concise description of the visual content
            2. Relevant hashtags
            3. Notable elements, themes, or patterns
            
            Then, synthesize all frames into:
            1. An overall video description
            2. Key themes and elements
            3. Most relevant hashtags for the entire video
            
            Format your response in a clear, structured way.
        """.trimIndent()

        val userPrompt = buildString {
            append("Analyzing the following video frames:\n\n")
            frames.forEachIndexed { index, frame ->
                append("Frame ${index + 1}: data:image/jpeg;base64,$frame\n")
            }
        }

        val chatCompletion = openAI.chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4-vision-preview"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = systemPrompt
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = userPrompt
                    )
                )
            )
        )

        return chatCompletion.choices.firstOrNull()?.message?.content ?: "No analysis available"
    }
}
