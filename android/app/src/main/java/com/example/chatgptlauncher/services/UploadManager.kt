package com.example.chatgptlauncher.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.chatgptlauncher.model.SocialPlatform
import com.example.chatgptlauncher.services.upload.InstagramUploader
import com.example.chatgptlauncher.services.upload.TikTokUploader
import com.example.chatgptlauncher.services.upload.YouTubeUploader
import com.example.chatgptlauncher.services.upload.VideoProcessor
import com.example.chatgptlauncher.services.upload.ContentGenerator
import com.example.chatgptlauncher.util.RetryManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.time.Duration.Companion.seconds

class UploadManager(
    private val context: Context,
    private val videoProcessor: VideoProcessor,
    private val contentGenerator: ContentGenerator,
    private val youTubeUploader: YouTubeUploader,
    private val instagramUploader: InstagramUploader,
    private val tiktokUploader: TikTokUploader,
    private val retryManager: RetryManager = RetryManager()
) {
    companion object {
        private const val TAG = "UploadManager"
    }

    data class UploadProgress(
        val platform: SocialPlatform,
        val stage: UploadStage,
        val progress: Float,
        val message: String? = null
    )

    enum class UploadStage {
        PROCESSING,
        GENERATING_CONTENT,
        UPLOADING,
        COMPLETED,
        ERROR
    }

    sealed class UploadError : Exception() {
        data class ProcessingError(
            override val message: String,
            override val cause: Throwable? = null
        ) : UploadError()
        
        data class ContentGenerationError(
            override val message: String,
            override val cause: Throwable? = null
        ) : UploadError()
        
        data class UploadError(
            override val message: String,
            override val cause: Throwable? = null
        ) : UploadError()
        
        data class ValidationError(
            override val message: String
        ) : UploadError()
    }

    fun uploadVideo(
        videoUri: Uri,
        platforms: List<SocialPlatform>,
        isCharityAccount: Boolean,
        videoContext: String
    ): Flow<Map<SocialPlatform, UploadProgress>> = flow {
        val progressMap = mutableMapOf<SocialPlatform, UploadProgress>()
        
        platforms.forEach { platform ->
            try {
                // Process Video with retry
                progressMap[platform] = UploadProgress(platform, UploadStage.PROCESSING, 0f)
                emit(progressMap.toMap())
                
                val processResult = retryManager.retryNetworkOperation(
                    maxAttempts = 3,
                    operation = {
                        videoProcessor.processVideo(videoUri, platform) { progress ->
                            progressMap[platform] = UploadProgress(platform, UploadStage.PROCESSING, progress)
                            emit(progressMap.toMap())
                        }
                    }
                )

                when (processResult) {
                    is VideoProcessor.ProcessingResult.Success -> {
                        // Generate Content with retry
                        progressMap[platform] = UploadProgress(platform, UploadStage.GENERATING_CONTENT, 0f)
                        emit(progressMap.toMap())
                        
                        val content = retryManager.retryNetworkOperation(
                            maxAttempts = 3,
                            operation = {
                                contentGenerator.generateContent(platform, isCharityAccount, videoContext)
                            }
                        )
                        
                        // Upload with rate limit handling
                        progressMap[platform] = UploadProgress(platform, UploadStage.UPLOADING, 0f)
                        emit(progressMap.toMap())
                        
                        val uploadUrl = retryManager.retryWithRateLimit(
                            maxAttempts = 5,
                            rateLimitDelay = 60.seconds,
                            operation = {
                                when (platform) {
                                    SocialPlatform.YOUTUBE -> youTubeUploader.upload(
                                        processResult.processedFile,
                                        content,
                                        isCharityAccount
                                    ) { progress ->
                                        progressMap[platform] = UploadProgress(
                                            platform,
                                            UploadStage.UPLOADING,
                                            progress
                                        )
                                        emit(progressMap.toMap())
                                    }
                                    
                                    SocialPlatform.INSTAGRAM -> instagramUploader.upload(
                                        processResult.processedFile,
                                        content,
                                        isCharityAccount
                                    ) { progress ->
                                        progressMap[platform] = UploadProgress(
                                            platform,
                                            UploadStage.UPLOADING,
                                            progress
                                        )
                                        emit(progressMap.toMap())
                                    }
                                    
                                    SocialPlatform.TIKTOK -> tiktokUploader.upload(
                                        processResult.processedFile,
                                        content,
                                        isCharityAccount
                                    ) { progress ->
                                        progressMap[platform] = UploadProgress(
                                            platform,
                                            UploadStage.UPLOADING,
                                            progress
                                        )
                                        emit(progressMap.toMap())
                                    }
                                }
                            }
                        )
                        
                        progressMap[platform] = UploadProgress(
                            platform,
                            UploadStage.COMPLETED,
                            1f,
                            "Upload complete: $uploadUrl"
                        )
                        emit(progressMap.toMap())
                    }
                    
                    is VideoProcessor.ProcessingResult.Error -> {
                        throw UploadError.ProcessingError(processResult.message)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading to $platform", e)
                val errorMessage = when (e) {
                    is UploadError -> e.message
                    is ContentGenerator.GenerationError -> "Content generation failed: ${e.message}"
                    else -> "Upload failed: ${e.message}"
                }
                
                progressMap[platform] = UploadProgress(
                    platform,
                    UploadStage.ERROR,
                    0f,
                    errorMessage
                )
                emit(progressMap.toMap())
            }
        }
    }
}
