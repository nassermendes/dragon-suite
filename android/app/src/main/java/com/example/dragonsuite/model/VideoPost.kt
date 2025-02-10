package com.example.dragonsuite.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "video_posts")
data class VideoPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val description: String,
    val hashtags: List<String>,
    val scheduledTime: LocalDateTime? = null,
    val isQueued: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
