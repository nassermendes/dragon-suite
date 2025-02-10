package com.example.dragonsuite.model

data class UploadResult(
    val platform: Platform,
    val account: Account,
    val success: Boolean,
    val message: String
)
