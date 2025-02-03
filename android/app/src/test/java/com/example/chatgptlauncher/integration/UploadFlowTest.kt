package com.example.chatgptlauncher.integration

import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.example.chatgptlauncher.auth.OAuthManager
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.model.SocialPlatform
import com.example.chatgptlauncher.services.*
import com.example.chatgptlauncher.services.upload.*
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
class UploadFlowTest {
    private lateinit var context: Context
    private lateinit var secureConfig: SecureConfig
    private lateinit var oAuthManager: OAuthManager
    private lateinit var uploadManager: UploadManager
    private lateinit var videoProcessor: VideoProcessor
    private lateinit var contentGenerator: ContentGenerator
    private lateinit var youTubeUploader: YouTubeUploader
    private lateinit var instagramUploader: InstagramUploader
    private lateinit var tiktokUploader: TikTokUploader

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        secureConfig = mockk(relaxed = true)
        oAuthManager = mockk()
        videoProcessor = mockk()
        contentGenerator = mockk()
        youTubeUploader = mockk()
        instagramUploader = mockk()
        tiktokUploader = mockk()

        uploadManager = UploadManager(
            context = context,
            videoProcessor = videoProcessor,
            contentGenerator = contentGenerator,
            youTubeUploader = youTubeUploader,
            instagramUploader = instagramUploader,
            tiktokUploader = tiktokUploader,
            retryManager = RetryManager()
        )
    }

    @Test
    fun `complete upload flow with authentication and retries`() = runTest {
        val videoUri = mockk<Uri>()
        val processedFile = mockk<File>()
        val content = ContentGenerator.GeneratedContent(
            title = "Test Video",
            caption = "Amazing content!",
            hashtags = listOf("#test", "#viral")
        )

        // Mock OAuth flow
        coEvery {
            oAuthManager.authenticateYouTube(any())
        } returns OAuthManager.AuthResult.Success(SocialPlatform.YOUTUBE)

        // Mock video processing with progress
        coEvery {
            videoProcessor.processVideo(any(), any(), any())
        } answers {
            val progressCallback = arg<(Float) -> Unit>(2)
            // Simulate progress updates
            progressCallback(0.25f)
            progressCallback(0.5f)
            progressCallback(0.75f)
            progressCallback(1.0f)
            VideoProcessor.ProcessingResult.Success(
                processedFile = processedFile,
                duration = 60L,
                aspectRatio = 1.77f,
                size = 1024 * 1024L
            )
        }

        // Mock content generation
        coEvery {
            contentGenerator.generateContent(any(), any(), any())
        } returns content

        // Mock YouTube upload with simulated rate limit then success
        var youtubeAttempts = 0
        coEvery {
            youTubeUploader.upload(any(), any(), any(), any())
        } answers {
            val progressCallback = arg<(Float) -> Unit>(3)
            youtubeAttempts++
            if (youtubeAttempts == 1) {
                throw retrofit2.HttpException(
                    retrofit2.Response.error<Any>(
                        429,
                        okhttp3.ResponseBody.create(null, "Rate limit exceeded")
                    )
                )
            }
            // Simulate upload progress
            progressCallback(0.33f)
            progressCallback(0.66f)
            progressCallback(1.0f)
            "https://youtube.com/watch?v=test123"
        }

        // Execute the upload flow
        uploadManager.uploadVideo(
            videoUri = videoUri,
            platforms = listOf(SocialPlatform.YOUTUBE),
            isCharityAccount = false,
            videoContext = "Test video about coding"
        ).test {
            // Verify initial processing state
            val initial = awaitItem()
            assertThat(initial[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.PROCESSING)

            // Collect all progress updates
            val stages = mutableListOf<UploadManager.UploadStage>()
            var item = awaitItem()
            while (item[SocialPlatform.YOUTUBE]?.stage != UploadManager.UploadStage.COMPLETED) {
                stages.add(item[SocialPlatform.YOUTUBE]?.stage!!)
                item = awaitItem()
            }

            // Verify flow progression
            assertThat(stages).containsAtLeast(
                UploadManager.UploadStage.PROCESSING,
                UploadManager.UploadStage.GENERATING_CONTENT,
                UploadManager.UploadStage.UPLOADING
            )

            // Verify final state
            assertThat(item[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.COMPLETED)
            assertThat(item[SocialPlatform.YOUTUBE]?.message)
                .contains("https://youtube.com/watch?v=test123")

            // Verify retry was used
            assertThat(youtubeAttempts).isEqualTo(2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle multiple platform uploads with mixed results`() = runTest {
        val videoUri = mockk<Uri>()
        val processedFile = mockk<File>()
        val content = ContentGenerator.GeneratedContent(
            caption = "Test content",
            hashtags = listOf("#test")
        )

        // Mock successful processing
        coEvery {
            videoProcessor.processVideo(any(), any(), any())
        } returns VideoProcessor.ProcessingResult.Success(
            processedFile = processedFile,
            duration = 30L,
            aspectRatio = 1.0f,
            size = 512L
        )

        coEvery {
            contentGenerator.generateContent(any(), any(), any())
        } returns content

        // YouTube succeeds
        coEvery {
            youTubeUploader.upload(any(), any(), any(), any())
        } returns "https://youtube.com/test"

        // Instagram fails
        coEvery {
            instagramUploader.upload(any(), any(), any(), any())
        } throws Exception("Upload failed")

        // TikTok succeeds after retry
        var tiktokAttempts = 0
        coEvery {
            tiktokUploader.upload(any(), any(), any(), any())
        } answers {
            tiktokAttempts++
            if (tiktokAttempts == 1) {
                throw retrofit2.HttpException(
                    retrofit2.Response.error<Any>(
                        503,
                        okhttp3.ResponseBody.create(null, "Service unavailable")
                    )
                )
            }
            "https://tiktok.com/test"
        }

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
            var finalStates = emptyMap<SocialPlatform, UploadManager.UploadProgress>()
            while (true) {
                val item = awaitItem()
                if (item.values.all { it.stage == UploadManager.UploadStage.COMPLETED || it.stage == UploadManager.UploadStage.ERROR }) {
                    finalStates = item
                    break
                }
            }

            // Verify YouTube success
            assertThat(finalStates[SocialPlatform.YOUTUBE]?.stage)
                .isEqualTo(UploadManager.UploadStage.COMPLETED)
            assertThat(finalStates[SocialPlatform.YOUTUBE]?.message)
                .contains("youtube.com")

            // Verify Instagram failure
            assertThat(finalStates[SocialPlatform.INSTAGRAM]?.stage)
                .isEqualTo(UploadManager.UploadStage.ERROR)
            assertThat(finalStates[SocialPlatform.INSTAGRAM]?.message)
                .contains("failed")

            // Verify TikTok success after retry
            assertThat(finalStates[SocialPlatform.TIKTOK]?.stage)
                .isEqualTo(UploadManager.UploadStage.COMPLETED)
            assertThat(finalStates[SocialPlatform.TIKTOK]?.message)
                .contains("tiktok.com")
            assertThat(tiktokAttempts).isEqualTo(2)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
