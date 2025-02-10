package com.example.dragonsuite.worker

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.example.dragonsuite.service.VideoPostManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ScheduledUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val videoPostManager = VideoPostManager(context)

    override suspend fun doWork(): Result {
        val videoId = inputData.getLong("video_id", -1)
        if (videoId == -1) return Result.failure()

        return try {
            val result = videoPostManager.postScheduledVideo(videoId)
            result.fold(
                onSuccess = { Result.success() },
                onFailure = { Result.failure() }
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun schedule(context: Context, videoId: Long, scheduledTime: LocalDateTime) {
            val delay = scheduledTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                    System.currentTimeMillis()
            
            if (delay <= 0) return

            val workRequest = OneTimeWorkRequestBuilder<ScheduledUploadWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("video_id" to videoId))
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "scheduled_upload_$videoId",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        fun cancel(context: Context, videoId: Long) {
            WorkManager.getInstance(context)
                .cancelUniqueWork("scheduled_upload_$videoId")
        }
    }
}
