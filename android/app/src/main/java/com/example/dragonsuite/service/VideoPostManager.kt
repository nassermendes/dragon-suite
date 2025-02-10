package com.example.dragonsuite.service

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.dragonsuite.database.AppDatabase
import com.example.dragonsuite.model.*
import com.example.dragonsuite.worker.ScheduledUploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.time.LocalDateTime

class VideoPostManager(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val videoPostDao = database.videoPostDao()
    private val platformTester = PlatformConnectionTester(context)
    private val client = OkHttpClient.Builder().build()

    fun getQueuedPosts(): Flow<List<VideoPost>> = videoPostDao.getQueuedPosts()
    fun getScheduledPosts(): Flow<List<VideoPost>> = videoPostDao.getScheduledPosts()

    suspend fun queueVideo(
        contentResolver: ContentResolver,
        videoUri: Uri,
        title: String,
        description: String,
        hashtags: List<String>
    ): Long {
        // Test upload capability first
        testVideoUpload(contentResolver, videoUri)
        
        val videoPost = VideoPost(
            uri = videoUri.toString(),
            title = title,
            description = description,
            hashtags = hashtags,
            isQueued = true
        )
        
        return videoPostDao.insert(videoPost)
    }

    suspend fun scheduleVideo(
        contentResolver: ContentResolver,
        videoUri: Uri,
        title: String,
        description: String,
        hashtags: List<String>,
        scheduledTime: LocalDateTime
    ): Result<Long> {
        // Validate scheduled time
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            return Result.failure(IllegalArgumentException("Cannot schedule video for past time"))
        }

        // Test upload capability
        testVideoUpload(contentResolver, videoUri)
        
        val videoPost = VideoPost(
            uri = videoUri.toString(),
            title = title,
            description = description,
            hashtags = hashtags,
            scheduledTime = scheduledTime
        )
        
        return try {
            val id = videoPostDao.insert(videoPost)
            
            // Schedule the upload
            ScheduledUploadWorker.schedule(context, id, scheduledTime)
            
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postNow(
        contentResolver: ContentResolver,
        videoUri: Uri,
        title: String,
        description: String,
        hashtags: List<String>
    ): Result<List<UploadResult>> {
        return try {
            // Test upload capability first
            testVideoUpload(contentResolver, videoUri)
            
            // Validate connections
            validateConnections().getOrThrow()
            
            // Create a temporary file for upload
            val tempFile = createTempFile(contentResolver, videoUri)
            
            val results = mutableListOf<UploadResult>()
            
            Platform.values().forEach { platform ->
                Account.values().forEach { account ->
                    try {
                        val result = uploadToPlatform(
                            platform = platform,
                            account = account,
                            videoFile = tempFile,
                            title = title,
                            description = description,
                            hashtags = hashtags
                        )
                        results.add(result)
                    } catch (e: Exception) {
                        results.add(
                            UploadResult(
                                platform = platform,
                                account = account,
                                success = false,
                                message = "Upload failed: ${e.message}"
                            )
                        )
                    }
                }
            }
            
            // Clean up temp file
            tempFile.delete()
            
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postQueuedVideo(id: Long): Result<List<UploadResult>> {
        val post = videoPostDao.getById(id) ?: return Result.failure(IllegalArgumentException("Video post not found"))
        
        return try {
            val uri = Uri.parse(post.uri)
            val result = postNow(
                context.contentResolver,
                uri,
                post.title,
                post.description,
                post.hashtags
            )
            
            // If successful, remove from queue
            if (result.isSuccess) {
                videoPostDao.delete(post)
            }
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postScheduledVideo(id: Long): Result<List<UploadResult>> {
        val post = videoPostDao.getById(id) ?: return Result.failure(IllegalArgumentException("Video post not found"))
        
        return try {
            val uri = Uri.parse(post.uri)
            val result = postNow(
                context.contentResolver,
                uri,
                post.title,
                post.description,
                post.hashtags
            )
            
            // If successful, remove from scheduled posts
            if (result.isSuccess) {
                videoPostDao.delete(post)
                // Cancel the scheduled work
                ScheduledUploadWorker.cancel(context, id)
            }
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postVideo(videoUri: Uri, title: String, description: String, hashtags: List<String>): List<UploadResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<UploadResult>()
        
        // Test upload to each platform
        val platforms = listOf(Platform.INSTAGRAM, Platform.YOUTUBE, Platform.TIKTOK)
        for (platform in platforms) {
            try {
                // Simulate posting to each platform
                val accounts = getAccountsForPlatform(platform)
                for (account in accounts) {
                    val result = uploadToPlatform(platform, account, videoUri, title, description, hashtags)
                    results.add(result)
                }
            } catch (e: Exception) {
                results.add(UploadResult(platform, Account.UNKNOWN, false, "Error: ${e.message}"))
            }
        }
        
        results
    }

    private fun getAccountsForPlatform(platform: Platform): List<Account> {
        return when (platform) {
            Platform.INSTAGRAM -> listOf(Account.MENDES, Account.CHARITY)
            Platform.YOUTUBE -> listOf(Account.MENDES, Account.CHARITY)
            Platform.TIKTOK -> listOf(Account.MENDES, Account.CHARITY)
        }
    }

    private suspend fun uploadToPlatform(platform: Platform, account: Account, videoUri: Uri, title: String, description: String, hashtags: List<String>): UploadResult {
        // This is a mock implementation. In a real app, you would:
        // 1. Get the appropriate API token for the platform and account
        // 2. Upload the video using the platform's API
        // 3. Return the actual result
        return UploadResult(platform, account, true, "Successfully uploaded to $platform for $account")
    }

    suspend fun clearQueue() {
        videoPostDao.clearQueue()
    }

    suspend fun clearScheduled() {
        videoPostDao.clearScheduled()
    }

    suspend fun testConnections(): Result<List<ConnectionTestResult>> {
        return try {
            Result.success(platformTester.testAllConnections())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun validateConnections(): Result<Unit> {
        val results = platformTester.testAllConnections()
        val failedConnections = results.filter { !it.isConnected }
        
        return if (failedConnections.isEmpty()) {
            Result.success(Unit)
        } else {
            val errorMessage = buildString {
                appendLine("Failed to connect to some platforms:")
                failedConnections.forEach { result ->
                    appendLine("- ${result.platform} (${result.account}): ${result.error}")
                }
            }
            Result.failure(IllegalStateException(errorMessage))
        }
    }

    private suspend fun testVideoUpload(contentResolver: ContentResolver, videoUri: Uri) {
        // Add your video validation logic here
        // For example:
        // - Check file size
        // - Verify video format
        // Throw exception if validation fails
    }

    private suspend fun uploadToPlatform(
        platform: Platform,
        account: Account,
        videoFile: File,
        title: String,
        description: String,
        hashtags: List<String>
    ): UploadResult {
        // Implement platform-specific upload logic here
        return UploadResult(
            platform = platform,
            account = account,
            success = true,
            message = "Upload successful",
            url = "https://example.com/video"
        )
    }

    private fun createTempFile(contentResolver: ContentResolver, videoUri: Uri): File {
        val inputStream = contentResolver.openInputStream(videoUri)
            ?: throw IllegalStateException("Could not open video file")
        
        val tempFile = File.createTempFile("upload", ".mp4")
        tempFile.outputStream().use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }
        
        return tempFile
    }
}
