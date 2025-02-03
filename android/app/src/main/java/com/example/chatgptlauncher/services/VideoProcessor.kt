package com.example.chatgptlauncher.services

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import com.arthenica.mobileffmpeg.MediaInformation
import java.io.File
import kotlin.math.min

class VideoProcessor(private val context: Context) {
    companion object {
        private const val TAG = "VideoProcessor"
        private const val ASPECT_RATIO_9_16 = 9f/16f
        private const val MAX_BITRATE = "2M"  // 2 Mbps
    }

    sealed class ProcessingResult {
        data class Success(
            val processedFile: File,
            val duration: Long,
            val aspectRatio: Float,
            val size: Long
        ) : ProcessingResult()
        
        data class Error(
            val message: String,
            val exception: Exception? = null
        ) : ProcessingResult()
    }

    private fun getMediaInfo(path: String): MediaInformation? {
        return try {
            FFprobe.getMediaInformation(path)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting media info", e)
            null
        }
    }

    private fun copyUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream for URI: $uri")
        
        val tempFile = File(context.cacheDir, "input_video_${System.currentTimeMillis()}.mp4")
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }

    suspend fun processVideo(
        inputUri: Uri,
        platform: SocialPlatform,
        progressCallback: (Float) -> Unit
    ): ProcessingResult {
        try {
            progressCallback(0.1f)
            
            // Copy input file to local storage
            val inputFile = copyUriToFile(inputUri)
            val mediaInfo = getMediaInfo(inputFile.path)
                ?: return ProcessingResult.Error("Failed to get media information")
            
            progressCallback(0.2f)
            
            // Get video dimensions
            val width = mediaInfo.streams[0].width ?: 1080
            val height = mediaInfo.streams[0].height ?: 1920
            val duration = mediaInfo.duration.toLongOrNull() ?: 0L
            
            // Check duration limits
            if (duration > platform.getMaxDuration()) {
                return ProcessingResult.Error("Video duration exceeds platform limit")
            }
            
            // Calculate new dimensions for 9:16 aspect ratio
            val (newWidth, newHeight) = calculateDimensions(width, height)
            
            // Process video
            val outputFile = File(context.cacheDir, "processed_${System.currentTimeMillis()}.mp4")
            
            val command = arrayOf(
                "-i", inputFile.path,
                "-vf", "scale=$newWidth:$newHeight,pad=$newWidth:$newHeight:(ow-iw)/2:(oh-ih)/2",
                "-c:v", "libx264",
                "-preset", "medium",
                "-b:v", MAX_BITRATE,
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                outputFile.path
            )
            
            val result = FFmpeg.execute(command)
            
            progressCallback(0.8f)
            
            if (result != 0) {
                return ProcessingResult.Error("FFmpeg processing failed with code: $result")
            }
            
            // Clean up input file
            inputFile.delete()
            
            progressCallback(1.0f)
            
            return ProcessingResult.Success(
                processedFile = outputFile,
                duration = duration,
                aspectRatio = ASPECT_RATIO_9_16,
                size = outputFile.length()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing video", e)
            return ProcessingResult.Error("Failed to process video: ${e.message}", e)
        }
    }
    
    private fun calculateDimensions(width: Int, height: Int): Pair<Int, Int> {
        val targetRatio = ASPECT_RATIO_9_16
        val currentRatio = width.toFloat() / height
        
        return when {
            currentRatio > targetRatio -> {
                // Video is too wide
                val newWidth = (height * targetRatio).toInt()
                Pair(newWidth, height)
            }
            currentRatio < targetRatio -> {
                // Video is too tall
                val newHeight = (width / targetRatio).toInt()
                Pair(width, newHeight)
            }
            else -> Pair(width, height)
        }
    }
}
