package com.example.dragonsuite.service

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class VideoUploadService {
    private val client = OkHttpClient.Builder().build()
    
    suspend fun uploadVideo(contentResolver: ContentResolver, videoUri: Uri, uploadUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Create a temporary file
            val tempFile = File.createTempFile("upload", ".mp4")
            
            // Copy the video content to the temporary file
            contentResolver.openInputStream(videoUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Create multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video",
                    "video.mp4",
                    tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )
                .build()
            
            // Create request
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()
            
            // Execute request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Upload failed: ${response.code}"))
                }
                
                return@withContext Result.success(response.body?.string() ?: "")
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}
