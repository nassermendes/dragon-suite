package com.example.dragonsuite.database.dao

import androidx.room.*
import com.example.dragonsuite.model.VideoPost
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoPostDao {
    @Query("SELECT * FROM video_posts WHERE isQueued = 1")
    fun getQueuedPosts(): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE scheduledTime IS NOT NULL")
    fun getScheduledPosts(): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE id = :id")
    suspend fun getById(id: Long): VideoPost?

    @Insert
    suspend fun insert(post: VideoPost): Long

    @Update
    suspend fun update(post: VideoPost)

    @Delete
    suspend fun delete(post: VideoPost)

    @Query("DELETE FROM video_posts WHERE isQueued = 1")
    suspend fun clearQueue()

    @Query("DELETE FROM video_posts WHERE scheduledTime IS NOT NULL")
    suspend fun clearScheduled()
}
