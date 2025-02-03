package com.example.chatgptlauncher.services

import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.example.chatgptlauncher.model.SocialPlatform
import com.example.chatgptlauncher.services.upload.InstagramUploader
import com.example.chatgptlauncher.services.upload.TikTokUploader
import com.example.chatgptlauncher.services.upload.YouTubeUploader
import com.example.chatgptlauncher.util.RetryManager
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class UploadManagerTest {
    private lateinit var uploadManager: UploadManager
    private lateinit var context: Context
    private lateinit var videoProcessor: VideoProcessor
    private lateinit var contentGenerator: ContentGenerator
    private lateinit var youTubeUploader: YouTubeUploader
    private lateinit var instagramUploader: InstagramUploader
    private lateinit var tiktokUploader: TikTokUploader
    private lateinit var retryManager: RetryManager

    @Before
    fun setup() {
        context = mockk()
        videoProcessor = mockk()
        contentGenerator = mockk()
        youTubeUploader = mockk()
        instagramUploader = mockk()
        tiktokUploader = mockk()
        retryManager = RetryManager()

        uploadManager = UploadManager(
            context = context,
            videoProcessor = videoProcessor,
            contentGenerator = contentGenerator,
            youTubeUploader = youTubeUploader,
            instagramUploader = instagramUploader,
            tiktokUploader = tiktokUploader,
            retryManager = retryManager
        )
    }

    @Test
    fun `uploadVideo should handle successful upload to all platforms`() = runTest {
        val videoUri = mockk<Uri>()
        val processedFile = mockk<File>()
        val content = ContentGenerator.GeneratedContent(
            caption = "Test caption",
            hashtags = listOf("#test"),
            title = "Test title"
        )

        // Mock successful video processing
        coEvery {
            videoProcessor.processVideo(
                any(),
                any(),
                any()
            )
        } returns VideoProcessor.ProcessingResult.Success(
            processedFile = processedFile,
            duration = 60L,
            aspectRatio = 1.0f,
            size = 1024L
        )

        // Mock content generation
        coEvery {
            contentGenerator.generateContent(any(), any(), any())
        } returns content

        // Mock successful uploads
        coEvery {
            youTubeUploader.upload(any(), any(), any(), any())
        } returns "https://youtube.com/watch?v=test"

        coEvery {
            instagramUploader.upload(any(), any(), any(), any())
        } returns "https://instagram.com/p/test"

        coEvery {
            tiktokUploader.upload(any(), any(), any(), any())
        } returns "https://tiktok.com/@user/video/test"

        uploadManager.uploadVideo(
            videoUri = videoUri,
            platforms = listOf(
                SocialPlatform.YOUTUBE,
                SocialPlatform.INSTAGRAM,
                SocialPlatform.TIKTOK
            ),
            isCharityAccount = false,
            videoContext = "Test video"
        ).test {
            // First emission: Processing started for all platforms
            val initial = awaitItem()
            assertThat(initial.size).isEqualTo(3)
            assertThat(initial.values).allMatch { 
                it.stage == UploadManager.UploadStage.PROCESSING 
            }

            // Multiple progress updates
            var item = awaitItem()
            while (item.values.any { it.stage != UploadManager.UploadStage.COMPLETED }) {
                item = awaitItem()
            }

            // Final emission: All uploads completed
            assertThat(item.size).isEqualTo(3)
            assertThat(item.values).allMatch { 
                it.stage == UploadManager.UploadStage.COMPLETED 
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uploadVideo should handle processing failure`() = runTest {
        val videoUri = mockk<Uri>()
        
        coEvery {
            videoProcessor.processVideo(any(), any(), any())
        } returns VideoProcessor.ProcessingResult.Error("Processing failed")

        uploadManager.uploadVideo(
            videoUri = videoUri,
            platforms = listOf(SocialPlatform.YOUTUBE),
            isCharityAccount = false,
            videoContext = "Test video"
        ).test {
            // Initial processing state
            val initial = awaitItem()
            assertThat(initial[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.PROCESSING)

            // Error state
            val error = awaitItem()
            assertThat(error[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.ERROR)
            assertThat(error[SocialPlatform.YOUTUBE]?.message)
                .contains("Processing failed")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uploadVideo should handle content generation failure`() = runTest {
        val videoUri = mockk<Uri>()
        val processedFile = mockk<File>()

        coEvery {
            videoProcessor.processVideo(any(), any(), any())
        } returns VideoProcessor.ProcessingResult.Success(
            processedFile = processedFile,
            duration = 60L,
            aspectRatio = 1.0f,
            size = 1024L
        )

        coEvery {
            contentGenerator.generateContent(any(), any(), any())
        } throws ContentGenerator.GenerationError.ApiError("API error")

        uploadManager.uploadVideo(
            videoUri = videoUri,
            platforms = listOf(SocialPlatform.YOUTUBE),
            isCharityAccount = false,
            videoContext = "Test video"
        ).test {
            // Processing state
            val processing = awaitItem()
            assertThat(processing[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.PROCESSING)

            // Content generation state
            val generating = awaitItem()
            assertThat(generating[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.GENERATING_CONTENT)

            // Error state
            val error = awaitItem()
            assertThat(error[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.ERROR)
            assertThat(error[SocialPlatform.YOUTUBE]?.message)
                .contains("Content generation failed")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uploadVideo should handle upload failure`() = runTest {
        val videoUri = mockk<Uri>()
        val processedFile = mockk<File>()
        val content = ContentGenerator.GeneratedContent(
            caption = "Test caption",
            hashtags = listOf("#test"),
            title = "Test title"
        )

        coEvery {
            videoProcessor.processVideo(any(), any(), any())
        } returns VideoProcessor.ProcessingResult.Success(
            processedFile = processedFile,
            duration = 60L,
            aspectRatio = 1.0f,
            size = 1024L
        )

        coEvery {
            contentGenerator.generateContent(any(), any(), any())
        } returns content

        coEvery {
            youTubeUploader.upload(any(), any(), any(), any())
        } throws Exception("Upload failed")

        uploadManager.uploadVideo(
            videoUri = videoUri,
            platforms = listOf(SocialPlatform.YOUTUBE),
            isCharityAccount = false,
            videoContext = "Test video"
        ).test {
            // Process through states until error
            while (true) {
                val item = awaitItem()
                if (item[SocialPlatform.YOUTUBE]?.stage == UploadManager.UploadStage.ERROR) {
                    assertThat(item[SocialPlatform.YOUTUBE]?.message)
                        .contains("Upload failed")
                    break
                }
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
